package com.inclusive.adaptiveeducationservice.api.fieldwork.researchidentity;

import com.inclusive.adaptiveeducationservice.fieldwork.application.researchidentity.ResearchConsentRequiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ResearchIdentityExceptionHandler {

    @ExceptionHandler(
            ResearchConsentRequiredException.class
    )
    public ResponseEntity<Void> handleResearchConsentRequired() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }
}
