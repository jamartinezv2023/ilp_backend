// notification-service/src/main/java/com/inclusive/notificationservice/domain/notification/NotificationService.java
package com.inclusive.notificationservice.domain.notification;

public interface NotificationService {

    Notification notifyStudent(Long studentId, String message);
}
