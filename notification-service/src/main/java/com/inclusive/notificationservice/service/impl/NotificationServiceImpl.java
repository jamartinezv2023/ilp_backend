package com.inclusive.notificationservice.service.impl;

import com.inclusive.notificationservice.dto.NotificationDTO;
import com.inclusive.notificationservice.entity.Notification;
import com.inclusive.notificationservice.mapper.NotificationMapper;
import com.inclusive.notificationservice.repository.NotificationRepository;
import com.inclusive.notificationservice.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Default implementation of NotificationService.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationDTO getNotificationById(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        return NotificationMapper.toDto(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserId(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByTenantAndUser(UUID tenantId, UUID userId) {
        return notificationRepository.findByTenantIdAndUserIdOrderByCreatedAtDesc(tenantId, userId)
                .stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationDTO createNotification(NotificationDTO dto) {
        Notification entity = NotificationMapper.toEntity(dto);
        entity.setId(null); // always create new
        Notification saved = notificationRepository.save(entity);
        return NotificationMapper.toDto(saved);
    }

    @Override
    public NotificationDTO updateNotification(UUID id, NotificationDTO dto) {
        Notification existing = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        existing.setTenantId(dto.getTenantId());
        existing.setUserId(dto.getUserId());
        existing.setTitle(dto.getTitle());
        existing.setMessage(dto.getMessage());

        if (dto.getType() != null) {
            existing.setType(Enum.valueOf(
                    com.inclusive.notificationservice.entity.NotificationType.class,
                    dto.getType()
            ));
        }

        if (dto.getChannel() != null) {
            existing.setChannel(Enum.valueOf(
                    com.inclusive.notificationservice.entity.NotificationChannel.class,
                    dto.getChannel()
            ));
        }

        if (dto.getPriority() != null) {
            existing.setPriority(Enum.valueOf(
                    com.inclusive.notificationservice.entity.NotificationPriority.class,
                    dto.getPriority()
            ));
        }

        existing.setMetadata(dto.getMetadata());
        if (dto.getRead() != null) {
            existing.setRead(dto.getRead());
        }
        if (dto.getDelivered() != null) {
            existing.setDelivered(dto.getDelivered());
        }
        if (dto.getArchived() != null) {
            existing.setArchived(dto.getArchived());
        }

        Notification updated = notificationRepository.save(existing);
        return NotificationMapper.toDto(updated);
    }

    @Override
    public NotificationDTO markAsRead(UUID id) {
        Notification existing = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        existing.setRead(true);
        Notification updated = notificationRepository.save(existing);
        return NotificationMapper.toDto(updated);
    }

    @Override
    public NotificationDTO archiveNotification(UUID id) {
        Notification existing = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        existing.setArchived(true);
        Notification updated = notificationRepository.save(existing);
        return NotificationMapper.toDto(updated);
    }

    @Override
    public void deleteNotification(UUID id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }
}
