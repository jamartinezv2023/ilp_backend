package com.inclusive.adaptiveeducationservice.api.fieldwork.researchidentity;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResearchIdentityResponse(
        UUID participantUuid,
        UUID researchSubjectId,
        LocalDateTime createdAt,
        boolean active
) {
}
