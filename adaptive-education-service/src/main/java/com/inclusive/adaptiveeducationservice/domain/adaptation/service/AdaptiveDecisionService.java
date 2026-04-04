package com.inclusive.adaptiveeducationservice.domain.adaptation.service;

import com.inclusive.common.events.v1.AssessmentCompletedEvent;

/**
 * Core decision engine contract.
 */
public interface AdaptiveDecisionService {

    /**
     * Processes an assessment completion and decides a recommendation.
     */
    void handleAssessmentCompleted(AssessmentCompletedEvent event);
}
