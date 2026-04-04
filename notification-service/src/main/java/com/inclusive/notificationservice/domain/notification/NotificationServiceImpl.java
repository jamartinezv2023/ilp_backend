// notification-service/src/main/java/com/inclusive/notificationservice/domain/notification/NotificationServiceImpl.java
package com.inclusive.notificationservice.domain.notification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    public NotificationServiceImpl(NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Notification notifyStudent(Long studentId, String message) {
        Notification notification = new Notification(studentId, message);
        return repository.save(notification);
    }
}
