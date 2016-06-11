package com.wetjens.springframework.social.garmin.api.internal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wetjens.springframework.social.garmin.api.GarminConnectException;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GarminConnectClient {

    private static final String LOGIN_URL = "https://sso.garmin.com/sso/login?service=https%3A%2F%2Fconnect.garmin.com%2Fpost-auth%2Flogin&clientId=GarminConnect&consumeServiceTicket=false";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36";

    private static final Pattern VALIDATION_URL_PATTERN = Pattern.compile("response_url\\s+=\\s+'([^']+)");
    private static final Pattern FLOW_EXECUTION_KEY_PATTERN = Pattern.compile("name=\"lt\"\\s+value=\"([^\"]+)\"");

    private final String username;
    private final String password;

    private final ObjectMapper objectMapper;
    private final CloseableHttpClient httpClient;

    private boolean authenticated;

    public GarminConnectClient(String username, String password) {
        this.username = username;
        this.password = password;

        this.httpClient = HttpClients.custom()
                .useSystemProperties()
                .disableRedirectHandling()
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void authenticate() {
        try {
            final String flowExecutionKey = this.getFlowExecutionKey();

            final String validationUrl = this.getAuthTicket(flowExecutionKey);

            this.validateTicket(validationUrl);

            this.authenticated = true;
        } catch (IOException e) {
            throw new GarminConnectException("Could not login to Garmin Connect", e);
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public <T> T callService(String url, Class<T> responseType) {
        if (!this.authenticated) {
            this.authenticate();
        }

        try {
            HttpGet request = new HttpGet(url);

            CloseableHttpResponse response = this.executeRequest(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
                this.authenticated = false;

                // reauthenticate
                this.authenticate();

                // try again
                response = this.executeRequest(request);
            }

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new GarminConnectException("Unexpected response from server: " + response.getStatusLine());
            }

            return this.objectMapper.readValue(response.getEntity().getContent(), responseType);
        } catch (IOException e) {
            throw new GarminConnectException("Could not get activities", e);
        }
    }

    private void validateTicket(String validationUrl) throws IOException {
        HttpGet request = new HttpGet(validationUrl);

        CloseableHttpResponse response = null;
        try {
            do {
                if (response != null) {
                    response.close();
                }

                response = this.executeRequest(request);

                final Header location = response.getFirstHeader("Location");
                if (location != null) {
                    request = new HttpGet(location.getValue());
                }
            } while (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new GarminConnectException("Could not validate ticket: " + response.getStatusLine());
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private CloseableHttpResponse executeRequest(HttpUriRequest request) throws IOException {
        request.setHeader("User-Agent", USER_AGENT);

        return this.httpClient.execute(request);
    }

    private String getAuthTicket(String flowExecutionKey) throws IOException {
        final HttpPost request = new HttpPost(LOGIN_URL);

        request.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("username", this.username),
                new BasicNameValuePair("password", this.password),
                new BasicNameValuePair("_eventId", "submit"),
                new BasicNameValuePair("embed", "true"),
                new BasicNameValuePair("lt", flowExecutionKey)
        )));

        final CloseableHttpResponse response = this.executeRequest(request);

        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new GarminConnectException("Could not get authentication ticket: " + response.getStatusLine());
            }

            final String content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");

            final Matcher matcher = VALIDATION_URL_PATTERN.matcher(content);
            if (matcher.find()) {
                return matcher.group(1);
            }

            throw new GarminConnectException("Authentication failed");
        } finally {
            response.close();
        }
    }

    private String getFlowExecutionKey() throws IOException {
        final HttpGet request = new HttpGet(LOGIN_URL);

        final CloseableHttpResponse response = this.executeRequest(request);

        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new GarminConnectException("Could not get flow execution key: " + response.getStatusLine());
            }

            final String content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");

            final Matcher matcher = FLOW_EXECUTION_KEY_PATTERN.matcher(content);
            if (matcher.find()) {
                return matcher.group(1);
            }

            throw new GarminConnectException("Could not find flow execution key in response body:\n" + content);
        } finally {
            response.close();
        }
    }
}
