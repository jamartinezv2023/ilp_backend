package com.inclusive.adaptiveeducationservice.fieldwork.service;

import java.util.UUID;

public final class ConsentRecordNotFoundException extends RuntimeException {

    public ConsentRecordNotFoundException(
            UUID consentId
    ) {
        super(
                "Consent record not found: "
                        + consentId
        );
    }
}