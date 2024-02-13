package com.cydeo.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class KeycloakProperties {
    //This is for retrieving K V from application.properties
    //All these properties will kept in GutHub,so you don't need to keep it in resources
    // These values will be required in other classes.
    //These structure will be used in microservices as well.
    //@Value("${keycloak.realm}")
    // Above realm will be read as cydeo-dev
    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${master.user}")
    private String masterUser;

    @Value("${master.user.password}")
    private String masterUserPswd;

    @Value("${master.realm}")
    private String masterRealm;

    @Value("${master.client}")
    private String masterClient;
}
