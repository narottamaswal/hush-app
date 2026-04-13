package com.hush.app.validation.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PostValidationException.class)
    public ResponseEntity<?> handlePostValidation(PostValidationException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getBody());
    }
}