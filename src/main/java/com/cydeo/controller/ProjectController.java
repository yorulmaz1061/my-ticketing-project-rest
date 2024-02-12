package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.entity.ResponseWrapper;
import com.cydeo.service.ProjectService;
import com.cydeo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {
    private final ProjectService projectService;


    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
    @GetMapping
    @RolesAllowed({"Manager","Admin"})
    public ResponseEntity<ResponseWrapper> getProjects() {
        List<ProjectDTO> projectDTOList= projectService.listAllProjects();
        return ResponseEntity.ok(new ResponseWrapper("Projects are successfully retrieved", projectDTOList, HttpStatus.OK));
    }
    @GetMapping("/{projectCode}")
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> getProjectsByCode(@PathVariable("projectCode") String projectCode) {
        ProjectDTO projectDTO = projectService.getByProjectCode(projectCode);
        return ResponseEntity.ok(new ResponseWrapper("Project is successfully retrieved", projectDTO, HttpStatus.OK));
    }
    @PostMapping
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> createProjects(@RequestBody ProjectDTO project) {
        projectService.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper("Project is successfully created", HttpStatus.CREATED));
    }
    @PutMapping
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> updateProjects(@RequestBody ProjectDTO project) {
        projectService.update(project);
        return ResponseEntity.ok(new ResponseWrapper("Project is successfully updated", project, HttpStatus.OK));
    }
    @DeleteMapping("/{projectCode}")
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> deleteProject(@PathVariable("projectCode") String code) {
        projectService.delete(code);
    return ResponseEntity.ok(new ResponseWrapper("User is successfully deleted", HttpStatus.OK));
    }
    //This project retrieves all projects belongs to certain manager.
    @GetMapping("/manager/project-status")
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> getProjectByManager() {
        //For now, listAllProjectDetails is hardcoded but it will be changed when we apply Keycloak
        List<ProjectDTO> projects = projectService.listAllProjectDetails();
        return ResponseEntity.ok(new ResponseWrapper("Projects are successfully retreived",projects, HttpStatus.OK));
    }

    @PutMapping("/manager/complete/{projectCode}")
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> managerCompleteProjects(@PathVariable("projectCode") String code) {
        projectService.complete(code);
        return ResponseEntity.ok(new ResponseWrapper("Projects is successfully completed", HttpStatus.OK));
    }









}
