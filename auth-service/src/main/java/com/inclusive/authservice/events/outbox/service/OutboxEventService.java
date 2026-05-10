package com.inclusive.authservice.events.outbox.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inclusive.authservice.events.outbox.domain.OutboxEvent;
import com.inclusive.authservice.events.outbox.domain.OutboxEventStatus;
import com.inclusive.authservice.events.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository repository;

    private final ObjectMapper objectMapper;

    @Transactional
    public void saveEvent(
            String aggregateType,
            String aggregateId,
            Object event
    ) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventType(event.getClass().getSimpleName())
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .payload(payload)
                    .status(OutboxEventStatus.PENDING)
                    .createdAt(OffsetDateTime.now())
                    .retryCount(0)
                    .build();

            repository.save(outboxEvent);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to serialize outbox event",
                    exception
            );
        }
    }
}
