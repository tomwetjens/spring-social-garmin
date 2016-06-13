# spring-social-garmin
Connect your Spring application with Garmin Connect API.

## Getting Started
Add the dependency to your pom.xml:
```xml
<dependency>
  <groupId>com.wetjens.springframework</groupId>
  <artifactId>spring-social-garmin</artifactId>
  <version>1.1.0</version>
</dependency>
```

Garmin Connect offers an API with OAuth 2.0 support, but is closed and paid.

This Spring Social integration simply logs the user into the Garmin Connect website with username and password and then
calls services that are also called from the Garmin Connect web application.

Therefore, instead of an access token, the user name and password of the user must be stored with the connection.
Make sure to use a ConnectionRepository implementation that stores the secret encrypted.

Add Garmin support to your Spring Social configuration:
```java
@Configuration
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter {

    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        // ...

        connectionFactoryConfigurer.addConnectionFactory(new GarminConnectConnectionFactory());
    }
}
```

## Connect
When using Spring Social, you normally add the ConnectController as a bean to your configuration.
Because Garmin Connect requires a different way of connecting, you have to also add the GarminConnectController as a bean.
```java
    @Bean
    @Autowired
    public GarminConnectController garminConnectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
        return new GarminConnectController(connectionFactoryLocator, connectionRepository);
    }
```

See http://docs.spring.io/spring-social/docs/1.0.3.RELEASE/reference/html/connecting.html.

To connect a user to Garmin Connect, do a request to the GarminConnectController:
```
POST /garmin/connect
Content-Type: application/x-www-form-urlencoded

username=<username>&password=<password>
```
This will return a 200 OK if the connection was successful, or a 4xx if the authentication failed or an unexpected error occurred.

Use the standard Spring Social ConnectController to view and manage connections.

## Sign in
When using Spring Social, you normally add the ProviderSignInController as a bean to your configuration.
Because Garmin Connect requires a different way of sign in, you have to also add the GarminSignInController as a bean.
```java
    @Bean
    @Autowired
    public GarminSigninController garminSigninController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository, SignInAdapter signInAdapter) {
        return new GarminSigninController(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
    }
```

You also need to set a SignInAdapter as described in http://docs.spring.io/spring-social/docs/1.0.3.RELEASE/reference/html/signin.html.

To sign in a user with Garmin Connect, do a request to the GarminSignInController:
```
POST /garmin/signin
Content-Type: application/x-www-form-urlencoded

username=<username>&password=<password>
```
This will return a 200 OK if the sign in was successful, or a 4xx if the authentication failed or an unexpected error occurred.

Currently it is expected that you provide a login form or send the request asynchronously.