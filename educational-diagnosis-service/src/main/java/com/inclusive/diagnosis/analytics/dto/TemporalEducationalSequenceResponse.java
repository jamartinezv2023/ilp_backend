package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record TemporalEducationalSequenceResponse(

        String studentId,

        List<TemporalEducationalSignalResponse> timeline,

        String ethicalTemporalPolicy
) {
}
