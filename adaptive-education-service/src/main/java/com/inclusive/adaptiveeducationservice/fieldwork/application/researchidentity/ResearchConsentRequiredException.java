package com.inclusive.adaptiveeducationservice.fieldwork.application.researchidentity;

import java.util.UUID;

public final class ResearchConsentRequiredException
        extends IllegalStateException {

    public ResearchConsentRequiredException(
            UUID participantUuid
    ) {
        super(
                "Active research consent is required for participant: "
                        + participantUuid
        );
    }
}
