package com.inclusive.authservice.events.outbox.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_outbox")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private String aggregateId;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxEventStatus status;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    private OffsetDateTime publishedAt;

    @Column(nullable = false)
    private Integer retryCount;

    public void markAsPublished() {
        this.status = OutboxEventStatus.PUBLISHED;
        this.publishedAt = OffsetDateTime.now();
    }

    public void markAsFailed() {
        this.status = OutboxEventStatus.FAILED;
        this.retryCount = this.retryCount + 1;
    }

    public void markAsPendingRetry() {
        this.status = OutboxEventStatus.PENDING;
        this.retryCount = this.retryCount + 1;
    }

    public void markAsDeadLetter() {
        this.status = OutboxEventStatus.DEAD_LETTER;
        this.retryCount = this.retryCount + 1;
    }
}
