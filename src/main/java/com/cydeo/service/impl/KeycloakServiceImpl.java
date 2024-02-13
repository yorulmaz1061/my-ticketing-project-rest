package com.cydeo.service.impl;

import com.cydeo.config.KeycloakProperties;
import com.cydeo.dto.UserDTO;
import com.cydeo.service.KeycloakService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.Arrays.asList;
import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

@Service
public class KeycloakServiceImpl implements KeycloakService {

//Injection for using keycloakProperties object.
    private final KeycloakProperties keycloakProperties;

    public KeycloakServiceImpl(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }
// Two services we need to override userCreate and delete.
//userCreate method: We assign a  password first.
//setTemporary(false); we don't wan to reset the password like we di it manually.
//user credential is assigning to keycloak we capture it from userDTO
    @Override
    public Response userCreate(UserDTO userDTO) {


        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(userDTO.getPassWord());
//Password is done upto here. Now keycloak properties will be filled by userDTO.
// UserRepresentation keeps keycloak properties
// Whatever I posted from json body via postman will be passed to keycloak.
        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(userDTO.getUserName());
        keycloakUser.setFirstName(userDTO.getFirstName());
        keycloakUser.setLastName(userDTO.getLastName());
        keycloakUser.setEmail(userDTO.getUserName());
        keycloakUser.setCredentials(asList(credential));
        keycloakUser.setEmailVerified(true);
        keycloakUser.setEnabled(true);

// If you do any activity in Keycloak you need to open a new keycloak instance
// And you need to close this instance when you are done. Look at concerned method below
// You open instance with getInstance() method.And pass some information like getAuthServerUrl()..etc.
// consider it like an opening keycloak page login
        Keycloak keycloak = getKeycloakInstance();

// After creating instance you need to specify which realm and which user by using below.
        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
        UsersResource usersResource = realmResource.users();

        // Create Keycloak user
        Response result = usersResource.create(keycloakUser);

        String userId = getCreatedId(result);
        ClientRepresentation appClient = realmResource.clients()
                .findByClientId(keycloakProperties.getClientId()).get(0);

        // Below it is matching the role json body role to keycloak role
        // But you need to first create a role in keycloak
        // If it doesn't match it will throw error.
        RoleRepresentation userClientRole = realmResource.clients().get(appClient.getId())
                .roles().get(userDTO.getRole().getDescription()).toRepresentation();

        realmResource.users().get(userId).roles().clientLevel(appClient.getId())
                .add(List.of(userClientRole));

// It is for closing keycloak
        keycloak.close();
        return result;
    }
// Below it is for delete action.
// First create instance, goto realm , getusers,
// Whenever user found it gets the id and delete it.
    @Override
    public void delete(String userName) {

        Keycloak keycloak = getKeycloakInstance();

        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
        UsersResource usersResource = realmResource.users();

        List<UserRepresentation> userRepresentations = usersResource.search(userName);
        String uid = userRepresentations.get(0).getId();
        usersResource.delete(uid);

        keycloak.close();
    }

    private Keycloak getKeycloakInstance(){
        return Keycloak.getInstance(keycloakProperties.getAuthServerUrl(),
                keycloakProperties.getMasterRealm(), keycloakProperties.getMasterUser()
                , keycloakProperties.getMasterUserPswd(), keycloakProperties.getMasterClient());
    }
}