package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalAdoptionReadinessResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationalAdoptionReadinessService {

    public EducationalAdoptionReadinessResponse generateAdoptionReadiness() {

        return new EducationalAdoptionReadinessResponse(
                "PILOT_READY",
                "REQUIRES_GUIDED_TRAINING",
                "MODERATELY_PREPARED",
                "CONTROLLED_AND_SUPERVISED",
                List.of(
                        "Teacher training on assistive AI interpretation",
                        "Institutional human oversight protocol",
                        "Data protection and anonymization policy",
                        "Bias and fairness review procedure",
                        "Pilot validation with anonymized educational evidence"
                ),
                "SUPERVISED_PILOT_DEPLOYMENT"
        );
    }
}
