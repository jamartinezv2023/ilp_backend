package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record ExpertValidationResponse(

        String expertValidationStatus,

        String pedagogicalExpertiseRequired,

        String inclusionExpertiseRequired,

        String aiEthicsExpertiseRequired,

        String technologyExpertiseRequired,

        List<String> validationCriteria,

        String recommendedAction
) {
}
