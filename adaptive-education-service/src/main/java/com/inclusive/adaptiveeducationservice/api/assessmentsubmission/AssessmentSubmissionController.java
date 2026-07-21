package com.inclusive.adaptiveeducationservice.api.assessmentsubmission;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission.SubmitAssessmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assessment-submissions")
public class AssessmentSubmissionController {

    private final SubmitAssessmentService submissionService;

    public AssessmentSubmissionController(
            SubmitAssessmentService submissionService
    ) {
        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<SubmitAssessmentResponse> submit(
            @Valid @RequestBody SubmitAssessmentRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(submissionService.submit(request));
    }
}