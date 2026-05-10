package com.inclusive.authservice.events.outbox.processor;

import com.inclusive.authservice.events.outbox.domain.OutboxEvent;
import com.inclusive.authservice.events.outbox.domain.OutboxEventStatus;
import com.inclusive.authservice.events.outbox.repository.OutboxEventRepository;
import com.inclusive.authservice.events.publisher.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisherProcessor {

    private final OutboxEventRepository repository;

    private static final int MAX_RETRY_ATTEMPTS = 3;

    private final DomainEventPublisher publisher;

    @Scheduled(fixedDelayString = "${events.outbox.publisher-delay-ms:5000}")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents =
                repository.findTop50ByStatusOrderByCreatedAtAsc(
                        OutboxEventStatus.PENDING
                );

        for (OutboxEvent event : pendingEvents) {
            publishEvent(event);
        }
    }

    private void publishEvent(
            OutboxEvent event
    ) {
        try {
            publisher.publish(
                    resolveTopic(event.getEventType()),
                    event.getPayload()
            );

            event.markAsPublished();

            repository.save(event);

        } catch (Exception exception) {
            if (event.getRetryCount() >= MAX_RETRY_ATTEMPTS) {
                event.markAsDeadLetter();
            } else {
                event.markAsPendingRetry();
            }

            repository.save(event);
        }
    }

    private String resolveTopic(
            String eventType
    ) {
        return switch (eventType) {
            case "AssessmentCompletedEvent" ->
                    "ilp.assessment.completed.v1";
            case "LearningPathInferenceRequestedEvent" ->
                    "ilp.learning-path.inference-requested.v1";
            case "LearningPathRecommendedEvent" ->
                    "ilp.learning-path.recommended.v1";
            default -> throw new IllegalArgumentException(
                    "Unknown event type: " + eventType
            );
        };
    }
}
