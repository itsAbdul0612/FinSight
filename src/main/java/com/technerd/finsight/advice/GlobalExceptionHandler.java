package com.technerd.finsight.advice;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception exception) {
        ApiError apiError = ApiError
                .builder()
                .message(exception.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleException(MethodArgumentNotValidException exception){
        List<String> errors = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        ApiError apiError = ApiError
                .builder()
                .message("Input Validation Error.").subErrors(errors)
                .status(HttpStatus.BAD_REQUEST).build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // Authentication Exceptions.

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException authException){

        ApiError apiError = ApiError
                .builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(authException.getMessage())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException accessDeniedException){

        ApiError apiError = ApiError
                .builder()
                .status(HttpStatus.FORBIDDEN)
                .message(accessDeniedException.getMessage())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    // JWT Exceptions

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiError> handleJWTExpiredException(ExpiredJwtException expiredJwtException){

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(expiredJwtException.getMessage()).build();

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiError> handleMalformedJWTException(SignatureException signatureException){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(signatureException.getMessage()).build();

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

}
