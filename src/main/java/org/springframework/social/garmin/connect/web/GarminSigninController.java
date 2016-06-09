package org.springframework.social.garmin.connect.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.MediaType;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.*;
import org.springframework.social.garmin.api.GarminConnect;
import org.springframework.social.garmin.connect.GarminConnectConnection;
import org.springframework.social.garmin.connect.GarminConnectConnectionFactory;
import org.springframework.social.support.URIBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.List;

/**
 * Spring MVC Controller for handling the Garmin user sign-in flow.
 * <p>
 * <ul>
 * <li>POST /garmin/signin  - Initiate user sign-in.</li>
 * </ul>
 *
 * @author Tom Wetjens
 * @see org.springframework.social.connect.web.ProviderSignInController
 */
@Controller
@RequestMapping("/garmin/signin")
public class GarminSigninController {

    private final static Log LOGGER = LogFactory.getLog(GarminSigninController.class);

    private final ConnectionFactoryLocator connectionFactoryLocator;

    private final UsersConnectionRepository usersConnectionRepository;

    private final SignInAdapter signInAdapter;

    private final MultiValueMap<Class<?>, ProviderSignInInterceptor<?>> signInInterceptors = new LinkedMultiValueMap<Class<?>, ProviderSignInInterceptor<?>>();

    private String signInUrl = "/signin";

    private String signUpUrl = "/signup";

    private String postSignInUrl = "/";

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    /**
     * Creates a new Garmin sign-in controller.
     *
     * @param connectionFactoryLocator  the locator of {@link ConnectionFactory connection factories} used to support provider sign-in.
     *                                  Note: this reference should be a serializable proxy to a singleton-scoped target instance.
     *                                  This is because {@link ProviderSignInAttempt} are session-scoped objects that hold ConnectionFactoryLocator references.
     *                                  If these references cannot be serialized, NotSerializableExceptions can occur at runtime.
     * @param usersConnectionRepository the global store for service provider connections across all users.
     *                                  Note: this reference should be a serializable proxy to a singleton-scoped target instance.
     * @param signInAdapter             handles user sign-in
     */
    @Autowired
    public GarminSigninController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository, SignInAdapter signInAdapter) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.usersConnectionRepository = usersConnectionRepository;
        this.signInAdapter = signInAdapter;
    }

    /**
     * Configure the list of sign in interceptors that should receive callbacks during the sign in process.
     * Convenient when an instance of this class is configured using a tool that supports JavaBeans-based configuration.
     *
     * @param interceptors the sign in interceptors to add
     */
    public void setSignInInterceptors(List<ProviderSignInInterceptor<?>> interceptors) {
        for (ProviderSignInInterceptor<?> interceptor : interceptors) {
            addSignInInterceptor(interceptor);
        }
    }

    /**
     * Adds a ConnectInterceptor to receive callbacks during the connection process.
     * Useful for programmatic configuration.
     *
     * @param interceptor the connect interceptor to add
     */
    public void addSignInInterceptor(ProviderSignInInterceptor<?> interceptor) {
        Class<?> serviceApiType = GenericTypeResolver.resolveTypeArgument(interceptor.getClass(), ProviderSignInInterceptor.class);
        signInInterceptors.add(serviceApiType, interceptor);
    }


    /**
     * Sets the URL of the application's sign in page.
     * Defaults to "/signin".
     *
     * @param signInUrl the signIn URL
     */
    public void setSignInUrl(String signInUrl) {
        this.signInUrl = signInUrl;
    }

    /**
     * Sets the URL to redirect the user to if no local user account can be mapped when signing in using a provider.
     * Defaults to "/signup".
     *
     * @param signUpUrl the signUp URL
     */
    public void setSignUpUrl(String signUpUrl) {
        this.signUpUrl = signUpUrl;
    }

    /**
     * Sets the default URL to redirect the user to after signing in using a provider.
     * Defaults to "/".
     *
     * @param postSignInUrl the postSignIn URL
     */
    public void setPostSignInUrl(String postSignInUrl) {
        this.postSignInUrl = postSignInUrl;
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

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView signIn(@RequestParam("username") String username, @RequestParam("password") String password, NativeWebRequest request) {
        try {
            GarminConnectConnectionFactory connectionFactory = (GarminConnectConnectionFactory) this.connectionFactoryLocator.getConnectionFactory(GarminConnectConnection.PROVIDER_ID);

            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
            preSignIn(connectionFactory, parameters, request);

            Connection<GarminConnect> connection = connectionFactory.createConnection(username, password);

            GarminConnect api = connection.getApi();
            // will throw exception if authentication fails
            api.authenticate();

            return handleSignIn(connection, connectionFactory, request);
        } catch (Exception e) {
            LOGGER.error("Exception while signing in to Garmin", e);
            return redirect(URIBuilder.fromUri(signInUrl).queryParam("error", "provider").build().toString());
        }
    }

    private RedirectView handleSignIn(Connection<?> connection, ConnectionFactory<?> connectionFactory, NativeWebRequest request) {
        List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
        if (userIds.size() == 0) {
            ProviderSignInAttempt signInAttempt = new ProviderSignInAttempt(connection);
            sessionStrategy.setAttribute(request, ProviderSignInAttempt.SESSION_ATTRIBUTE, signInAttempt);
            return redirect(signUpUrl);
        } else if (userIds.size() == 1) {
            usersConnectionRepository.createConnectionRepository(userIds.get(0)).updateConnection(connection);
            String originalUrl = signInAdapter.signIn(userIds.get(0), connection, request);
            postSignIn(connectionFactory, connection, (WebRequest) request);
            return originalUrl != null ? redirect(originalUrl) : redirect(postSignInUrl);
        } else {
            return redirect(URIBuilder.fromUri(signInUrl).queryParam("error", "multiple_users").build().toString());
        }
    }

    private RedirectView redirect(String url) {
        return new RedirectView(url, true);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void preSignIn(ConnectionFactory<?> connectionFactory, MultiValueMap<String, String> parameters, WebRequest request) {
        for (ProviderSignInInterceptor interceptor : interceptingSignInTo(connectionFactory)) {
            interceptor.preSignIn(connectionFactory, parameters, request);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void postSignIn(ConnectionFactory<?> connectionFactory, Connection<?> connection, WebRequest request) {
        for (ProviderSignInInterceptor interceptor : interceptingSignInTo(connectionFactory)) {
            interceptor.postSignIn(connection, request);
        }
    }

    private List<ProviderSignInInterceptor<?>> interceptingSignInTo(ConnectionFactory<?> connectionFactory) {
        Class<?> serviceType = GenericTypeResolver.resolveTypeArgument(connectionFactory.getClass(), ConnectionFactory.class);
        List<ProviderSignInInterceptor<?>> typedInterceptors = signInInterceptors.get(serviceType);
        if (typedInterceptors == null) {
            typedInterceptors = Collections.emptyList();
        }
        return typedInterceptors;
    }

}
