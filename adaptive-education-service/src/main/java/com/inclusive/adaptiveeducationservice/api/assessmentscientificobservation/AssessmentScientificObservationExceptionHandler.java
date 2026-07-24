package com.inclusive.adaptiveeducationservice.api.assessmentscientificobservation;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.AssessmentScientificObservationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(
        assignableTypes =
                AssessmentScientificObservationController.class
)
public class AssessmentScientificObservationExceptionHandler {

    @ExceptionHandler(
            AssessmentScientificObservationNotFoundException.class
    )
    public ProblemDetail handleNotFound(
            AssessmentScientificObservationNotFoundException exception
    ) {
        ProblemDetail detail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND,
                        exception.getMessage()
                );

        detail.setTitle(
                "Scientific assessment observation not found"
        );

        return detail;
    }

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
                "Invalid scientific observation request"
        );

        return detail;
    }
}