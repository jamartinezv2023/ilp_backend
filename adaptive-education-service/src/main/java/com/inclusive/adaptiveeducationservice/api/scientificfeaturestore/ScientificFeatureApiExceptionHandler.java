package com.inclusive.adaptiveeducationservice.api.scientificfeaturestore;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestControllerAdvice(
        assignableTypes = ScientificFeatureVectorController.class
)
public class ScientificFeatureApiExceptionHandler {

    @ExceptionHandler(
            MissingServletRequestParameterException.class
    )
    public ResponseEntity<ScientificFeatureApiErrorResponse>
    handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "SCIENTIFIC_FEATURE_MISSING_PARAMETER",
                "Required parameter is missing: "
                        + exception.getParameterName(),
                request
        );
    }

    @ExceptionHandler(
            MethodArgumentTypeMismatchException.class
    )
    public ResponseEntity<ScientificFeatureApiErrorResponse>
    handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        String parameterName =
                exception.getName() == null
                        ? "request parameter"
                        : exception.getName();

        return build(
                HttpStatus.BAD_REQUEST,
                "SCIENTIFIC_FEATURE_INVALID_PARAMETER_FORMAT",
                parameterName
                        + " has an invalid format",
                request
        );
    }

    @ExceptionHandler(
            IllegalArgumentException.class
    )
    public ResponseEntity<ScientificFeatureApiErrorResponse>
    handleInvalidRequest(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "SCIENTIFIC_FEATURE_INVALID_REQUEST",
                safeMessage(
                        exception,
                        "The scientific feature request is invalid"
                ),
                request
        );
    }

    @ExceptionHandler(
            ResponseStatusException.class
    )
    public ResponseEntity<ScientificFeatureApiErrorResponse>
    handleResponseStatus(
            ResponseStatusException exception,
            HttpServletRequest request
    ) {
        HttpStatus status =
                HttpStatus.resolve(
                        exception.getStatusCode().value()
                );

        HttpStatus resolvedStatus =
                status == null
                        ? HttpStatus.INTERNAL_SERVER_ERROR
                        : status;

        String code =
                resolvedStatus == HttpStatus.NOT_FOUND
                        ? "SCIENTIFIC_FEATURE_NOT_FOUND"
                        : "SCIENTIFIC_FEATURE_HTTP_ERROR";

        return build(
                resolvedStatus,
                code,
                safeReason(
                        exception,
                        resolvedStatus.getReasonPhrase()
                ),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ScientificFeatureApiErrorResponse>
    handleUnexpectedError(
            Exception exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "SCIENTIFIC_FEATURE_INTERNAL_ERROR",
                "An unexpected scientific feature error occurred",
                request
        );
    }

    private ResponseEntity<ScientificFeatureApiErrorResponse> build(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request
    ) {
        ScientificFeatureApiErrorResponse response =
                new ScientificFeatureApiErrorResponse(
                        Instant.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        code,
                        message,
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    private String safeMessage(
            Exception exception,
            String fallback
    ) {
        String message = exception.getMessage();

        return message == null || message.isBlank()
                ? fallback
                : message;
    }

    private String safeReason(
            ResponseStatusException exception,
            String fallback
    ) {
        String reason = exception.getReason();

        return reason == null || reason.isBlank()
                ? fallback
                : reason;
    }
}
