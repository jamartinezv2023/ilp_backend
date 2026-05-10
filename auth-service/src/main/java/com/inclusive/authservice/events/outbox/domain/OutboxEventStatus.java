package com.inclusive.authservice.events.outbox.domain;

public enum OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILED,
    DEAD_LETTER
}
