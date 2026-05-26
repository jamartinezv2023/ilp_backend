package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalResilienceResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationalResilienceService {

    public EducationalResilienceResponse generateOperationalResilience() {

        return new EducationalResilienceResponse(

                "HIGH_RESILIENCE",

                "MAINTAINED",

                "ACTIVE",

                "CONTROLLED",

                List.of(
                        "Human oversight fallback",
                        "Institutional governance enforcement",
                        "Adaptive monitoring continuity",
                        "Traceability preservation",
                        "Non-clinical safeguard policies"
                ),

                "STABLE"
        );
    }
}
