package com.cydeo.service;

import com.cydeo.dto.UserDTO;

import javax.ws.rs.core.Response;

public interface KeycloakService {
    //Get userDTO from postman and create in Keycloak
    Response userCreate(UserDTO userDTO);
    void delete(String userName);
}
