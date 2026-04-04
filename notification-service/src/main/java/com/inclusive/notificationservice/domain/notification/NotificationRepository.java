// notification-service/src/main/java/com/inclusive/notificationservice/domain/notification/NotificationRepository.java
package com.inclusive.notificationservice.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
