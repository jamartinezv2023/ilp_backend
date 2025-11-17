package com.inclusive.notificationservice.repository;

import com.inclusive.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Notification persistence and queries.
 */
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Notification> findByTenantIdAndUserIdOrderByCreatedAtDesc(UUID tenantId, UUID userId);
}
