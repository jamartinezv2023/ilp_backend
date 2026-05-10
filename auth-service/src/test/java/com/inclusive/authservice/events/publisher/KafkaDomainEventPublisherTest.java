package com.inclusive.authservice.events.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

class KafkaDomainEventPublisherTest {

    @Test
    void shouldSerializeAndPublishEvent() {
        KafkaTemplate<String, String> kafkaTemplate =
                mock(KafkaTemplate.class);

        KafkaDomainEventPublisher publisher =
                new KafkaDomainEventPublisher(
                        kafkaTemplate,
                        new ObjectMapper()
                );

        assertDoesNotThrow(() ->
                publisher.publish(
                        EventTopics.ASSESSMENT_COMPLETED_V1,
                        Map.of(
                                "schemaVersion", "v1",
                                "eventType", "AssessmentCompletedEvent"
                        )
                )
        );
    }
}
