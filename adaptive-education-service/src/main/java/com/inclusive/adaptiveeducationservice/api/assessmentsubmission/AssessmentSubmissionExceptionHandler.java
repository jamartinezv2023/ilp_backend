package com.inclusive.adaptiveeducationservice.api.assessmentsubmission;

import com.inclusive.adaptiveeducationservice.api.assessment.KolbAssessmentController;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.InvalidAssessmentSubmissionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {
        AssessmentSubmissionController.class, KolbAssessmentController.class
})
public class AssessmentSubmissionExceptionHandler {

    @ExceptionHandler(InvalidAssessmentSubmissionException.class)
    public ProblemDetail handleInvalidSubmission(
            InvalidAssessmentSubmissionException exception
    ) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "The assessment responses do not satisfy the instrument rules."
        );
        detail.setTitle("Invalid assessment submission");
        detail.setProperty("code", "ASSESSMENT_SUBMISSION_INVALID");
        return detail;
    }
}
