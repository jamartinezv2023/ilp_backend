package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record AdaptiveSupportProfileResponse(

        String studentProfileId,

        List<SupportDimensionResponse> supportDimensions,

        String ethicalWarning
) {
}
