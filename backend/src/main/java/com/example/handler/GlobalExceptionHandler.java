package com.example.handler;

import java.util.*;
import com.example.entity.ApiError;
import com.example.entity.RootEntity;
import com.example.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import jakarta.validation.ConstraintViolation;
import com.example.exception.UserNotFoundException;
import com.example.exception.TaskNotFoundException;
import com.example.exception.TaskUserMismatchException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.naming.AuthenticationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    public <T> ApiError<T> createApiError(T message, WebRequest request){
        ApiError<T> apiError = new ApiError<>();
        apiError.setId(UUID.randomUUID().toString());
        apiError.setErrorTime(new Date());
        apiError.setErrorMessages(message);
        apiError.setPath(request.getDescription(false).substring(4));
        return apiError;
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RootEntity<ApiError> handleBadCredentialsException(BadCredentialsException ex, WebRequest request){
        return RootEntity.error(createApiError(ex.getMessage(),request),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RootEntity<ApiError> handleAuthenticationException(AuthenticationException ex, WebRequest request){
        return RootEntity.error(createApiError(ex.getMessage(),request),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RootEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request){

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .toList();
        return RootEntity.error(createApiError(errors,request),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RootEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request){
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        return RootEntity.error(createApiError(errors,request),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RootEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {

        List<String> errors = new ArrayList<>();

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            errors.add("Invalid value for '" + ex.getName() + "'. Allowed values are: " +
                    Arrays.toString(ex.getRequiredType().getEnumConstants()));
        } else {
            errors.add(ex.getMessage());
        }

        return RootEntity.error(createApiError(errors,request),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RootEntity<ApiError> handleTaskNotFoundException(TaskNotFoundException ex, WebRequest request){
        return RootEntity.error(createApiError(ex.getMessage(),request),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RootEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex, WebRequest request){
        return RootEntity.error(createApiError(ex.getMessage(),request),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TaskUserMismatchException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public RootEntity<ApiError> handleTaskUserMismatchException(TaskUserMismatchException ex, WebRequest request){
        return RootEntity.error(createApiError(ex.getMessage(),request),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public RootEntity<ApiError> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request){
        return RootEntity.error(createApiError(ex.getMessage(),request),HttpStatus.CONFLICT);
    }

}
