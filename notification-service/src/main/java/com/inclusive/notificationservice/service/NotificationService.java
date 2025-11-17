package com.inclusive.notificationservice.service;

import com.inclusive.notificationservice.dto.NotificationDTO;

import java.util.List;
import java.util.UUID;

/**
 * Domain service for Notification.
 */
public interface NotificationService {

    List<NotificationDTO> getAllNotifications();

    NotificationDTO getNotificationById(UUID id);

    List<NotificationDTO> getNotificationsByUserId(UUID userId);

    List<NotificationDTO> getNotificationsByTenantAndUser(UUID tenantId, UUID userId);

    NotificationDTO createNotification(NotificationDTO dto);

    NotificationDTO updateNotification(UUID id, NotificationDTO dto);

    NotificationDTO markAsRead(UUID id);

    NotificationDTO archiveNotification(UUID id);

    void deleteNotification(UUID id);
}
