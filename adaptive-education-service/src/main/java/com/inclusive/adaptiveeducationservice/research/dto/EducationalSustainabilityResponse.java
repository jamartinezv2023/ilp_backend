package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record EducationalSustainabilityResponse(

        String sustainabilityLevel,

        String institutionalAdoptionReadiness,

        String pedagogicalSustainability,

        String ethicalSustainability,

        List<String> sustainabilityFactors,

        String longTermOperationalStatus
) {
}
