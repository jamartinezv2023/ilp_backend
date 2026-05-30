package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.ResearchInstrumentsValidationResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResearchInstrumentsValidationService {

    public ResearchInstrumentsValidationResponse generateInstrumentsValidationPreview() {

        return new ResearchInstrumentsValidationResponse(
                "READY_FOR_INSTRUMENT_DESIGN_AND_CONTENT_VALIDATION",
                "TEACHER_PERCEPTION_AND_USEFULNESS_SURVEY_REQUIRED",
                "EXPERT_JUDGMENT_RUBRIC_REQUIRED",
                "SEMI_STRUCTURED_INTERVIEW_GUIDE_REQUIRED",
                "CLASSROOM_OBSERVATION_PROTOCOL_REQUIRED",
                "USABILITY_AND_EXPLAINABILITY_EVALUATION_REQUIRED",
                List.of(
                        "Pedagogical relevance",
                        "Inclusive education alignment",
                        "Teacher decision support",
                        "Explainability of recommendations",
                        "Perceived usefulness",
                        "Ethical clarity",
                        "Usability and accessibility"
                ),
                "Design and validate teacher survey, expert rubric, interview guide and observation protocol before pilot deployment."
        );
    }
}
