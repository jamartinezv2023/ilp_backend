package com.inclusive.adaptiveeducationservice.api.assessmentrenderer;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.AssessmentDefinitionNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice(
        assignableTypes = AssessmentRendererController.class
)
public class AssessmentRendererExceptionHandler {

    @ExceptionHandler(
            AssessmentDefinitionNotFoundException.class
    )
    public ResponseEntity<AssessmentRendererErrorResponse>
    handleNotFound(
            AssessmentDefinitionNotFoundException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        new AssessmentRendererErrorResponse(
                                Instant.now(),
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND
                                        .getReasonPhrase(),
                                exception.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AssessmentRendererErrorResponse>
    handleBadRequest(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new AssessmentRendererErrorResponse(
                                Instant.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST
                                        .getReasonPhrase(),
                                exception.getMessage(),
                                request.getRequestURI()
                        )
                );
    }
}