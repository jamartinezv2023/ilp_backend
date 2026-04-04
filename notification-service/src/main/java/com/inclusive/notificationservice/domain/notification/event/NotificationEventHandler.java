package com.inclusive.notificationservice.domain.notification.event;

import com.inclusive.common.events.v1.LearningPathRecommendedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * In-process handler (MVP). Later replace with Kafka/Rabbit consumer.
 */
@Component
public class NotificationEventHandler {

    @EventListener
    public void onLearningPathRecommended(LearningPathRecommendedEvent event) {
        // For now, just a placeholder.
        // Here you would create and persist a Notification entity and dispatch via email/push/etc.
        // Key for research: correlationId to trace full chain.
        System.out.println(
                "[notification-service] correlationId=" + event.correlationId()
                        + " studentId=" + event.studentId()
                        + " items=" + event.recommendedItems()
                        + " confidence=" + event.confidence()
                        + " policy=" + event.policyName()
        );
    }
}
