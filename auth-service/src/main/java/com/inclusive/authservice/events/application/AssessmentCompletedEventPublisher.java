package com.inclusive.authservice.events.application;

import com.inclusive.authservice.events.publisher.DomainEventPublisher;
import com.inclusive.authservice.events.publisher.EventTopics;
import com.inclusive.common.events.v1.AssessmentCompletedEvent;
import org.springframework.stereotype.Service;

@Service
public class AssessmentCompletedEventPublisher {

    private final DomainEventPublisher domainEventPublisher;

    public AssessmentCompletedEventPublisher(
            DomainEventPublisher domainEventPublisher
    ) {
        this.domainEventPublisher = domainEventPublisher;
    }

    public void publish(
            AssessmentCompletedEvent event
    ) {
        domainEventPublisher.publish(
                EventTopics.ASSESSMENT_COMPLETED_V1,
                event
        );
    }
}
