package com.inclusive.adaptiveeducationservice.fieldwork.controller;

import com.inclusive.adaptiveeducationservice.fieldwork.service.ConsentRecordNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class FieldworkExceptionHandler {

    @ExceptionHandler(ConsentRecordNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleConsentNotFound(
            ConsentRecordNotFoundException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of(
                                "status",
                                HttpStatus.NOT_FOUND.value(),
                                "error",
                                "Not Found",
                                "message",
                                exception.getMessage()
                        )
                );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTransition(
            IllegalStateException exception
    ) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "status",
                                HttpStatus.BAD_REQUEST.value(),
                                "error",
                                "Bad Request",
                                "message",
                                exception.getMessage()
                        )
                );
    }
}