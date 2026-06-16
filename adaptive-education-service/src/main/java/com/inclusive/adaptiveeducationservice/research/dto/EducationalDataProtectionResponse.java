package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record EducationalDataProtectionResponse(

        String dataProtectionLevel,

        String sensitiveDataPolicy,

        String anonymizationStatus,

        String accessControlRequirement,

        List<String> protectedDataCategories,

        String privacyRiskStatus
) {
}
