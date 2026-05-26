package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalSustainabilityResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationalSustainabilityService {

    public EducationalSustainabilityResponse generateSustainabilityPreview() {

        return new EducationalSustainabilityResponse(

                "LONGITUDINALLY_SUSTAINABLE",

                "HIGH",

                "STABLE",

                "CONTROLLED",

                List.of(
                        "Teacher-centered assistive AI",
                        "Human oversight preserved",
                        "Institutional governance enabled",
                        "Inclusive educational alignment",
                        "Ethical non-clinical boundaries"
                ),

                "READY_FOR_PILOT_SCALE_VALIDATION"
        );
    }
}
