package com.inclusive.adaptiveeducationservice.api.assessmentrenderer;

import java.time.Instant;

public record AssessmentRendererErrorResponse(

        Instant timestamp,

        int status,

        String error,

        String message,

        String path
) {
}