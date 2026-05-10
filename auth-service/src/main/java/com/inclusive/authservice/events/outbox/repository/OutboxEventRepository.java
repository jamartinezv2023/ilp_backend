package com.inclusive.authservice.events.outbox.repository;

import com.inclusive.authservice.events.outbox.domain.OutboxEvent;
import com.inclusive.authservice.events.outbox.domain.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(
            OutboxEventStatus status
    );
}
