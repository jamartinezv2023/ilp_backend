package com.inclusive.adaptiveeducationservice.api.assessmentsubmission;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

public record SubmitAssessmentQuestionRequest(

        @NotBlank
        String questionCode,

        List<String> selectedOptionIds,

        Map<String, Integer> rankings,

        Double numericValue,

        String textValue
) {

    public SubmitAssessmentQuestionRequest {
        selectedOptionIds = selectedOptionIds == null
                ? List.of()
                : List.copyOf(selectedOptionIds);

        rankings = rankings == null
                ? Map.of()
                : Map.copyOf(rankings);
    }
}
