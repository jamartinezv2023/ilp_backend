package com.inclusive.notificationservice.mapper;

import com.inclusive.notificationservice.dto.NotificationDTO;
import com.inclusive.notificationservice.entity.Notification;
import com.inclusive.notificationservice.entity.NotificationChannel;
import com.inclusive.notificationservice.entity.NotificationPriority;
import com.inclusive.notificationservice.entity.NotificationType;

/**
 * Utility mapper for converting between Notification entity and DTO.
 */
public final class NotificationMapper {

    private NotificationMapper() {
        // Utility class
    }

    public static Notification toEntity(NotificationDTO dto) {
        if (dto == null) {
            return null;
        }
        Notification entity = new Notification();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setUserId(dto.getUserId());
        entity.setTitle(dto.getTitle());
        entity.setMessage(dto.getMessage());

        if (dto.getType() != null) {
            entity.setType(NotificationType.valueOf(dto.getType()));
        }
        if (dto.getChannel() != null) {
            entity.setChannel(NotificationChannel.valueOf(dto.getChannel()));
        }
        if (dto.getPriority() != null) {
            entity.setPriority(NotificationPriority.valueOf(dto.getPriority()));
        }

        entity.setMetadata(dto.getMetadata());
        entity.setRead(Boolean.TRUE.equals(dto.getRead()));
        entity.setDelivered(Boolean.TRUE.equals(dto.getDelivered()));
        entity.setArchived(Boolean.TRUE.equals(dto.getArchived()));
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

    public static NotificationDTO toDto(Notification entity) {
        if (entity == null) {
            return null;
        }
        NotificationDTO dto = new NotificationDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setUserId(entity.getUserId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setType(entity.getType() != null ? entity.getType().name() : null);
        dto.setChannel(entity.getChannel() != null ? entity.getChannel().name() : null);
        dto.setPriority(entity.getPriority() != null ? entity.getPriority().name() : null);
        dto.setMetadata(entity.getMetadata());
        dto.setRead(entity.isRead());
        dto.setDelivered(entity.isDelivered());
        dto.setArchived(entity.isArchived());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
