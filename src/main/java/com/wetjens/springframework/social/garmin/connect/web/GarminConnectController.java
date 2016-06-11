package com.wetjens.springframework.social.garmin.connect.web;

import com.wetjens.springframework.social.garmin.connect.GarminConnectConnection;
import com.wetjens.springframework.social.garmin.connect.GarminConnectConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.MediaType;
import org.springframework.social.InvalidAuthorizationException;
import org.springframework.social.connect.*;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import com.wetjens.springframework.social.garmin.api.GarminConnect;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * Generic UI controller for managing the account-to-service-provider connection flow.
 * <ul>
 * <li>POST /garmin/connect - Initiate an connection with Garmin.</li>
 * </ul>
 *
 * @author Tom Wetjens
 * @see org.springframework.social.connect.web.ConnectController
 */
@Controller
@RequestMapping("/garmin/connect")
public class GarminConnectController {

    private final static Log LOGGER = LogFactory.getLog(GarminConnectController.class);

    private final ConnectionFactoryLocator connectionFactoryLocator;

    private final ConnectionRepository connectionRepository;

    private final MultiValueMap<Class<?>, ConnectInterceptor<?>> connectInterceptors = new LinkedMultiValueMap<Class<?>, ConnectInterceptor<?>>();

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    /**
     * Constructs the controller.
     *
     * @param connectionFactoryLocator the locator for {@link ConnectionFactory} instances needed to establish connections
     * @param connectionRepository     the current user's {@link ConnectionRepository} needed to persist connections; must be a proxy to a request-scoped bean
     */
    @Autowired
    public GarminConnectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.connectionRepository = connectionRepository;
    }

    /**
     * Configure the list of connect interceptors that should receive callbacks during the connection process.
     * Convenient when an instance of this class is configured using a tool that supports JavaBeans-based configuration.
     *
     * @param interceptors the connect interceptors to add
     * @deprecated Use {@link #setConnectInterceptors(List)} instead.
     */
    @Deprecated
    public void setInterceptors(List<ConnectInterceptor<?>> interceptors) {
        setConnectInterceptors(interceptors);
    }

    /**
     * Configure the list of connect interceptors that should receive callbacks during the connection process.
     * Convenient when an instance of this class is configured using a tool that supports JavaBeans-based configuration.
     *
     * @param interceptors the connect interceptors to add
     */
    public void setConnectInterceptors(List<ConnectInterceptor<?>> interceptors) {
        for (ConnectInterceptor<?> interceptor : interceptors) {
            addInterceptor(interceptor);
        }
    }

    /**
     * Sets a strategy to use when persisting information that is to survive past the boundaries of a request.
     * The default strategy is to set the data as attributes in the HTTP Session.
     *
     * @param sessionStrategy the session strategy.
     */
    public void setSessionStrategy(SessionStrategy sessionStrategy) {
        this.sessionStrategy = sessionStrategy;
    }

    /**
     * Adds a ConnectInterceptor to receive callbacks during the connection process.
     * Useful for programmatic configuration.
     *
     * @param interceptor the connect interceptor to add
     */
    public void addInterceptor(ConnectInterceptor<?> interceptor) {
        Class<?> serviceApiType = GenericTypeResolver.resolveTypeArgument(interceptor.getClass(), ConnectInterceptor.class);
        connectInterceptors.add(serviceApiType, interceptor);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView connect(@RequestParam("username") String username, @RequestParam("password") String password, NativeWebRequest request) {
        try {
            GarminConnectConnectionFactory connectionFactory = (GarminConnectConnectionFactory) this.connectionFactoryLocator.getConnectionFactory(GarminConnectConnection.PROVIDER_ID);

            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
            preConnect(connectionFactory, parameters, request);

            Connection<GarminConnect> connection = connectionFactory.createConnection(username, password);

            if (!connection.test()) {
                throw new InvalidAuthorizationException(GarminConnectConnection.PROVIDER_ID, "Authentication failed");
            }

            this.addConnection(connection, connectionFactory, request);
        } catch (Exception e) {
            sessionStrategy.setAttribute(request, PROVIDER_ERROR_ATTRIBUTE, e);
            LOGGER.warn("Exception while connecting to Garmin. Redirecting to " + GarminConnectConnection.PROVIDER_ID + " connection status page.");
        }
        return connectionStatusRedirect(GarminConnectConnection.PROVIDER_ID, request);
    }

    /**
     * Returns a RedirectView with the URL to redirect to after a connection is created or deleted.
     * Defaults to "/connect/{providerId}" relative to DispatcherServlet's path.
     * May be overridden to handle custom redirection needs.
     *
     * @param providerId the ID of the provider for which a connection was created or deleted.
     * @param request    the NativeWebRequest used to access the servlet path when constructing the redirect path.
     * @return a RedirectView to the page to be displayed after a connection is created or deleted
     */
    protected RedirectView connectionStatusRedirect(String providerId, NativeWebRequest request) {
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        String path = "/connect/" + providerId + getPathExtension(servletRequest);
        if (prependServletPath(servletRequest)) {
            path = servletRequest.getServletPath() + path;
        }
        return new RedirectView(path, true);
    }

    private boolean prependServletPath(HttpServletRequest request) {
        return !this.urlPathHelper.getPathWithinServletMapping(request).equals("");
    }

    /*
     * Determines the path extension, if any.
     * Returns the extension, including the period at the beginning, or an empty string if there is no extension.
     * This makes it possible to append the returned value to a path even if there is no extension.
     */
    private String getPathExtension(HttpServletRequest request) {
        String fileName = WebUtils.extractFullFilenameFromUrlPath(request.getRequestURI());
        String extension = StringUtils.getFilenameExtension(fileName);
        return extension != null ? "." + extension : "";
    }

    private void addConnection(Connection<?> connection, ConnectionFactory<?> connectionFactory, WebRequest request) {
        try {
            this.connectionRepository.addConnection(connection);

            postConnect(connectionFactory, connection, request);
        } catch (DuplicateConnectionException e) {
            sessionStrategy.setAttribute(request, DUPLICATE_CONNECTION_ATTRIBUTE, e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void preConnect(ConnectionFactory<?> connectionFactory, MultiValueMap<String, String> parameters, WebRequest request) {
        for (ConnectInterceptor interceptor : interceptingConnectionsTo(connectionFactory)) {
            interceptor.preConnect(connectionFactory, parameters, request);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void postConnect(ConnectionFactory<?> connectionFactory, Connection<?> connection, WebRequest request) {
        for (ConnectInterceptor interceptor : interceptingConnectionsTo(connectionFactory)) {
            interceptor.postConnect(connection, request);
        }
    }

    private List<ConnectInterceptor<?>> interceptingConnectionsTo(ConnectionFactory<?> connectionFactory) {
        Class<?> serviceType = GenericTypeResolver.resolveTypeArgument(connectionFactory.getClass(), ConnectionFactory.class);
        List<ConnectInterceptor<?>> typedInterceptors = connectInterceptors.get(serviceType);
        if (typedInterceptors == null) {
            typedInterceptors = Collections.emptyList();
        }
        return typedInterceptors;
    }

    protected static final String DUPLICATE_CONNECTION_ATTRIBUTE = "social_addConnection_duplicate";

    protected static final String PROVIDER_ERROR_ATTRIBUTE = "social_provider_error";

}
