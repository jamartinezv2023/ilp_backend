package com.inclusive.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public void notify(String channel, String message) {
        log.info("Ã°Å¸â€â€ Notification [{}]: {}", channel, message);
    }
}
