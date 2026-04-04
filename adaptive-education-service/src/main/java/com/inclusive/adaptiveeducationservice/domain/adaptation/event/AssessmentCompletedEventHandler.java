package com.inclusive.adaptiveeducationservice.domain.adaptation.event;

import com.inclusive.adaptiveeducationservice.domain.adaptation.service.AdaptiveDecisionService;
import com.inclusive.common.events.v1.AssessmentCompletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Input handler (in-process).
 * In real microservices, this would be a message consumer.
 */
@Component
public class AssessmentCompletedEventHandler {

    private final AdaptiveDecisionService decisionService;

    public AssessmentCompletedEventHandler(AdaptiveDecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @EventListener
    public void onAssessmentCompleted(AssessmentCompletedEvent event) {
        decisionService.handleAssessmentCompleted(event);
    }
}
