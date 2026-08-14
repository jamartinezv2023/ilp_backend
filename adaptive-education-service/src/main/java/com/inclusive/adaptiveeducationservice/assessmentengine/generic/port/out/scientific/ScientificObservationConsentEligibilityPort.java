package com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific;

@FunctionalInterface
public interface ScientificObservationConsentEligibilityPort {

    boolean hasActiveConsentForParticipantCode(
            String participantCode
    );
}