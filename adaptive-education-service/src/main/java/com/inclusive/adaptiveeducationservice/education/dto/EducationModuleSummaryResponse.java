package com.inclusive.adaptiveeducationservice.education.dto;

import java.util.List;

public record EducationModuleSummaryResponse(

        String module,

        String audience,

        String purpose,

        String operationalStatus,

        List<String> capabilities,

        List<String> nextActions
) {
}
