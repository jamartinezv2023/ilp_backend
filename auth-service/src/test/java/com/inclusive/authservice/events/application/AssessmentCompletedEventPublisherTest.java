package com.inclusive.authservice.events.application;

import com.inclusive.authservice.events.publisher.DomainEventPublisher;
import com.inclusive.authservice.events.publisher.EventTopics;
import com.inclusive.common.events.v1.AssessmentCompletedEvent;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AssessmentCompletedEventPublisherTest {

    @Test
    void shouldPublishAssessmentCompletedEventToExpectedTopic() {
        DomainEventPublisher domainEventPublisher =
                mock(DomainEventPublisher.class);

        AssessmentCompletedEventPublisher publisher =
                new AssessmentCompletedEventPublisher(domainEventPublisher);

        AssessmentCompletedEvent event = AssessmentCompletedEvent.of(
                UUID.randomUUID(),
                1L,
                2L,
                "tenant-a",
                "KOLB",
                95.0,
                "PASSED",
                OffsetDateTime.now()
        );

        publisher.publish(event);

        verify(domainEventPublisher).publish(
                EventTopics.ASSESSMENT_COMPLETED_V1,
                event
        );
    }
}
