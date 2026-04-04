package com.inclusive.notificationservice.event;

import com.inclusive.notificationservice.service.NotificationService;
import org.springframework.stereotype.Component;
import com.inclusive.common.events.v1.AssessmentCompletedEvent;


@Component("integrationNotificationHandler")
public class NotificationEventHandler {

    private final NotificationService notificationService;

    public NotificationEventHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void handle(String eventType, String payload) {
        notificationService.notify(
                "LOG",
                "Event=" + eventType + " Payload=" + payload
        );
    }
}
