package com.inclusive.adaptiveeducationservice.domain.adaptation.service;

import com.inclusive.common.events.v1.AssessmentCompletedEvent;

public interface AdaptiveDecisionService {
    void handleAssessmentCompleted(AssessmentCompletedEvent event);
}
