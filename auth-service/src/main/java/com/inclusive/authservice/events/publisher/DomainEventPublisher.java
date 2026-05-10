package com.inclusive.authservice.events.publisher;

public interface DomainEventPublisher {

    void publish(
            String topic,
            Object event
    );
}
