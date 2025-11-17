package com.inclusive.notificationservice.controller;

import com.inclusive.notificationservice.dto.NotificationDTO;
import com.inclusive.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller that exposes CRUD and utility endpoints
 * for notifications.
 *
 * Base path: /api/notifications
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * GET /api/notifications
     * Returns all notifications.
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    /**
     * GET /api/notifications/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    /**
     * GET /api/notifications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }

    /**
     * GET /api/notifications/tenant/{tenantId}/user/{userId}
     * Multi-tenant aware query.
     */
    @GetMapping("/tenant/{tenantId}/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getByTenantAndUser(
            @PathVariable("tenantId") UUID tenantId,
            @PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByTenantAndUser(tenantId, userId));
    }

    /**
     * POST /api/notifications
     * Creates a new notification.
     */
    @PostMapping
    public ResponseEntity<NotificationDTO> create(@Valid @RequestBody NotificationDTO dto) {
        NotificationDTO created = notificationService.createNotification(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/notifications/{id}
     * Full update.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NotificationDTO> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody NotificationDTO dto) {
        NotificationDTO updated = notificationService.updateNotification(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH /api/notifications/{id}/read
     * Marks notification as read.
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable("id") UUID id) {
        NotificationDTO updated = notificationService.markAsRead(id);
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH /api/notifications/{id}/archive
     * Archives a notification.
     */
    @PatchMapping("/{id}/archive")
    public ResponseEntity<NotificationDTO> archive(@PathVariable("id") UUID id) {
        NotificationDTO updated = notificationService.archiveNotification(id);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
