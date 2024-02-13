package com.cydeo.exception;

import com.cydeo.annotation.DefaultExceptionMessage;
import com.cydeo.dto.DefaultExceptionMessageDTO;
import com.cydeo.entity.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Optional;
//Interceptor.Whenever any exception happens in the application, that exception needs to come here.
@RestControllerAdvice
public class GlobalExceptionHandler {
// If in the application any exception happens belongs to ticketing project exception class
    // which I created, it will throw this method to work
    // message is below.
    @ExceptionHandler(TicketingProjectException.class)
    public ResponseEntity<ResponseWrapper> serviceException(TicketingProjectException se){
        String message = se.getMessage();
        return new ResponseEntity<>(ResponseWrapper.builder().success(false).code(HttpStatus.CONFLICT.value()).message(message).build(),HttpStatus.CONFLICT);
    }
    // If any exception happens belongs to access denied class the method below will be executed.
    // Output is stated in return line.

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseWrapper> accessDeniedException(AccessDeniedException se){
        String message = se.getMessage();
        return new ResponseEntity<>(ResponseWrapper.builder().success(false).code(HttpStatus.FORBIDDEN.value()).message(message).build(),HttpStatus.FORBIDDEN);
    }
// For all other exception classes the method below will run.
    // If you have an annotation in method level, if statement will run
    // in this case the message wil be shown whatever in the annotation.
    // But if you don't have annotation then other statement message will be displayed.
    @ExceptionHandler({Exception    .class, RuntimeException.class, Throwable.class, BadCredentialsException.class})
    public ResponseEntity<ResponseWrapper> genericException(Throwable e, HandlerMethod handlerMethod) {

        Optional<DefaultExceptionMessageDTO> defaultMessage = getMessageFromAnnotation(handlerMethod.getMethod());
        if (defaultMessage.isPresent() && !ObjectUtils.isEmpty(defaultMessage.get().getMessage())) {
            ResponseWrapper response = ResponseWrapper
                    .builder()
                    .success(false)
                    .message(defaultMessage.get().getMessage())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ResponseWrapper.builder().success(false).message("Action failed: An error occurred!").code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    private Optional<DefaultExceptionMessageDTO> getMessageFromAnnotation(Method method) {
        DefaultExceptionMessage defaultExceptionMessage = method.getAnnotation(DefaultExceptionMessage.class);
        if (defaultExceptionMessage != null) {
            DefaultExceptionMessageDTO defaultExceptionMessageDto = DefaultExceptionMessageDTO
                    .builder()
                    .message(defaultExceptionMessage.defaultMessage())
                    .build();
            return Optional.of(defaultExceptionMessageDto);
        }
        return Optional.empty();
    }
}
