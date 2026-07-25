package com.inclusive.adaptiveeducationservice.api.assessmentscientifichistory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(
        assignableTypes =
                ParticipantAssessmentScientificHistoryController.class
)
public class ParticipantAssessmentScientificHistoryExceptionHandler {

    @ExceptionHandler(
            IllegalArgumentException.class
    )
    public ProblemDetail handleInvalidRequest(
            IllegalArgumentException exception
    ) {
        ProblemDetail detail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage()
                );

        detail.setTitle(
                "Invalid scientific history request"
        );

        return detail;
    }

    @ExceptionHandler(
            IllegalStateException.class
    )
    public ProblemDetail handleInconsistentHistory(
            IllegalStateException exception
    ) {
        ProblemDetail detail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        exception.getMessage()
                );

        detail.setTitle(
                "Scientific history integrity error"
        );

        return detail;
    }
}