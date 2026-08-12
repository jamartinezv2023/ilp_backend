package com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity;

import java.util.UUID;

@FunctionalInterface
public interface ResearchConsentEligibilityQueryPort {

    boolean hasActiveConsent(
            UUID participantUuid
    );
}
