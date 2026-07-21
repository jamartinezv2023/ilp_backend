package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentQuestionRequest;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubmitAssessmentMapper {

    public AssessmentSubmission toDomain(
            SubmitAssessmentRequest request
    ) {
        List<AssessmentResponse> responses =
                request.responses()
                        .stream()
                        .map(this::toDomainResponse)
                        .toList();

        return new AssessmentSubmission(
                request.administrationId(),
                request.participantId(),
                request.assessmentCode(),
                request.assessmentVersion(),
                responses,
                request.context(),
                request.submittedAt()
        );
    }

    private AssessmentResponse toDomainResponse(
            SubmitAssessmentQuestionRequest response
    ) {
        return new AssessmentResponse(
                response.questionCode(),
                response.selectedOptionIds(),
                response.rankings(),
                response.numericValue(),
                response.textValue()
        );
    }
}