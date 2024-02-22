package com.cydeo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Aspect
@Configuration
@Slf4j
public class LoggingAspect {

   // Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    //no need this one if you put @slf4j

    // With this code below, I can get authorized person from security context holder
    // After that I have authorized person
    private String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SimpleKeycloakAccount details = (SimpleKeycloakAccount) authentication.getDetails();

        return details.getKeycloakSecurityContext().getToken().getPreferredUsername();
    }

    //I explain aspect where to go for checking business logic.
    // using execution, it can return anything, i need to give address,
    // I check method in project controller or task controller
    //* all method I want to check and (..) any parameter type
    @Pointcut("execution(* com.cydeo.controller.ProjectController.*(..)) || execution(* com.cydeo.controller.TaskController.*(..))")
    private void anyControllerOperation() {
    }

    //First Let's practice Before and After advice
    //Write method name of pointcut as a parameter of Before advice.
    @Before("anyControllerOperation()")
    //after that write method name and pass joinpoint as a parameter
    public void anyBeforeControllerOperationAdvice(JoinPoint joinPoint) {
        // Get authorized person
        String username = getUserName();

        log.info("Before()-> User : {} - Method: {} - Parameters: {}", username, joinPoint.getSignature(), joinPoint.getArgs());

    }

    // I will not change pointcut and do it for @AfterReturning
    //AfterReturning works with K=V
    @AfterReturning(pointcut = "anyControllerOperation()", returning = "results")
    public void anyAfterControllerOperationAdvice(JoinPoint joinPoint, Object results) {
        String username = getUserName();
        log.info("AfterReturning -> User : {} - Method : {} - Results: {}", username, joinPoint.getSignature().toShortString(), results.toString());
    }

    //Second parameter will be throwing.
    @AfterThrowing(pointcut = "anyControllerOperation()", throwing = "exception")
    public void anyAfterControllerOperationAdvice(JoinPoint joinPoint, RuntimeException exception) {
        String username = getUserName();
        log.info("AfterThrowing -> User : {} - Method : {} - Exception: {}", username, joinPoint.getSignature().toShortString(), exception.getMessage());
    }


}
