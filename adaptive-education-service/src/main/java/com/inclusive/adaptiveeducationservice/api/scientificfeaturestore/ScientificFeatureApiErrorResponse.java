package com.inclusive.adaptiveeducationservice.api.scientificfeaturestore;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Objects;

@Schema(
        name = "ScientificFeatureApiError",
        description = "Uniform error returned by the Scientific Feature Store REST API."
)
public record ScientificFeatureApiErrorResponse(
        @Schema(
                description = "Instant at which the error response was created.",
                type = "string",
                format = "date-time",
                example = "2026-08-02T22:56:00Z"
        )
        Instant timestamp,

        @Schema(
                description = "HTTP status code.",
                example = "400",
                minimum = "400",
                maximum = "599"
        )
        int status,

        @Schema(
                description = "Standard HTTP error reason.",
                example = "Bad Request"
        )
        String error,

        @Schema(
                description = "Stable machine-readable application error code.",
                example = "SCIENTIFIC_FEATURE_INVALID_REQUEST"
        )
        String code,

        @Schema(
                description = "Human-readable error description.",
                example = "participantId must not be blank"
        )
        String message,

        @Schema(
                description = "Request path that produced the error.",
                example = "/api/v1/scientific-feature-store/exact"
        )
        String path
) {

    public ScientificFeatureApiErrorResponse {
        Objects.requireNonNull(
                timestamp,
                "timestamp is required"
        );

        if (status < 400 || status > 599) {
            throw new IllegalArgumentException(
                    "status must represent an HTTP error"
            );
        }

        error = requireText(error, "error");
        code = requireText(code, "code");
        message = requireText(message, "message");
        path = requireText(path, "path");
    }

    private static String requireText(
            String value,
            String field
    ) {
        Objects.requireNonNull(
                value,
                field + " is required"
        );

        String normalized = value.trim();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    field + " must not be blank"
            );
        }

        return normalized;
    }
}
