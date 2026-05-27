package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.PilotValidationReadinessResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PilotValidationReadinessService {

    public PilotValidationReadinessResponse generatePilotValidationReadiness() {

        return new PilotValidationReadinessResponse(
                "READY_FOR_SUPERVISED_PILOT",
                "ANONYMIZED_DATASET_REQUIRED",
                "TEACHER_FEEDBACK_PROTOCOL_REQUIRED",
                "MANDATORY_BEFORE_REAL_STUDENT_DATA_USE",
                List.of(
                        "Explainability perceived by teachers",
                        "Pedagogical coherence of recommendations",
                        "Ethical non-clinical boundary compliance",
                        "Human oversight effectiveness",
                        "Institutional auditability"
                ),
                "Start supervised pilot validation only with anonymized educational evidence and institutional approval."
        );
    }
}
