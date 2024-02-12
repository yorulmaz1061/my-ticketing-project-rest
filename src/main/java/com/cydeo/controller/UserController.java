package com.cydeo.controller;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.ResponseWrapper;
import com.cydeo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
//we add version number
//with one end point you can implement all CRUD
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    //Build crud operators
    // create method always return type is ResponseEntity
    // use ResponseWrapper to make some form in json body
    //ResponseEntity<ResponseWrapper> enable us to get custom output

    //Below retrieve all users:
    @GetMapping
    @RolesAllowed("Admin")
    public ResponseEntity<ResponseWrapper> getUsers(){
        List<UserDTO> userDTOList = userService.listAllUsers();
        //inside ok() you need to pass responsewrapper object
        return ResponseEntity.ok(new ResponseWrapper("Users are successfully retrieved",userDTOList, HttpStatus.OK));

    }
    //Below get specific user:
    @GetMapping("/{userName}")
    @RolesAllowed("Admin")
    public ResponseEntity<ResponseWrapper> getUserByUserName(@PathVariable("userName") String userName){
        UserDTO user = userService.findByUserName(userName);
        return ResponseEntity.ok(new ResponseWrapper("User is successfully retrieved", user, HttpStatus.OK));
    }
    // create user:
    @PostMapping
    @RolesAllowed("Admin")
    //If you need to catch something from Postman, you need to catch it with @RequestBody
    public ResponseEntity<ResponseWrapper> createUser(@RequestBody UserDTO user){
    userService.save(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper("User is successfully created", HttpStatus.CREATED));
    }
    //update user:
    @PutMapping
    @RolesAllowed("Admin")
    //If you need to catch something from Postman, you need to catch it with @RequestBody
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserDTO user){
        userService.update(user);
        return ResponseEntity.ok(new ResponseWrapper("User is successfully updated",user,HttpStatus.OK));
    }
    @DeleteMapping("/{userName}")
    @RolesAllowed("Admin")
    public ResponseEntity<ResponseWrapper> deleteUser(@PathVariable ("userName") String userName){
        userService.deleteByUserName(userName);
        return ResponseEntity.ok(new ResponseWrapper("User is successfully deleted", HttpStatus.OK));
        //204 - HttpStatus.No_CONTENT
        // If you use 204 (NO CONTENT) body is not shown.
        //return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseWrapper("User is successfully deleted", HttpStatus.OK));

    }

}
