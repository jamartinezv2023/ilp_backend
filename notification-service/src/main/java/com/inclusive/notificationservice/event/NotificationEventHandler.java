package com.inclusive.notificationservice.event;

import com.inclusive.notificationservice.service.NotificationService;
import org.springframework.stereotype.Component;


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
