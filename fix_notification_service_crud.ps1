Param(
    [string]$ProjectRoot = "$(Get-Location)"
)

Write-Host "ProjectRoot: $ProjectRoot"

$moduleRoot      = Join-Path $ProjectRoot "notification-service"
$baseJava        = Join-Path $moduleRoot "src\main\java\com\inclusive\notificationservice"
$entityDir       = Join-Path $baseJava "entity"
$dtoDir          = Join-Path $baseJava "dto"
$mapperDir       = Join-Path $baseJava "mapper"
$repositoryDir   = Join-Path $baseJava "repository"
$serviceDir      = Join-Path $baseJava "service"
$serviceImplDir  = Join-Path $serviceDir "impl"
$controllerDir   = Join-Path $baseJava "controller"

# 1. Crear directorios necesarios
$dirs = @(
    $entityDir,
    $dtoDir,
    $mapperDir,
    $repositoryDir,
    $serviceDir,
    $serviceImplDir,
    $controllerDir
)

foreach ($d in $dirs) {
    if (-not (Test-Path $d)) {
        New-Item -ItemType Directory -Path $d | Out-Null
        Write-Host "[DIR] Created: $d"
    } else {
        Write-Host "[DIR] Exists:  $d"
    }
}

# 2. Eliminar archivos viejos / corruptos
$oldFiles = @(
    (Join-Path $controllerDir "NotificationController.java"),
    (Join-Path $dtoDir       "NotificationDTO.java"),
    (Join-Path $entityDir    "Notification.java"),
    (Join-Path $mapperDir    "NotificationMapper.java"),
    (Join-Path $repositoryDir "NotificationRepository.java"),
    (Join-Path $serviceDir    "NotificationService.java"),
    (Join-Path $serviceImplDir "NotificationServiceImpl.java"),
    (Join-Path $moduleRoot "src\main\java\controller\notification-serviceController.java")
)

foreach ($f in $oldFiles) {
    if (Test-Path $f) {
        Remove-Item $f -Force
        Write-Host "[DEL] $f"
    }
}

# 3. Escribir entidad Notification + enums

$notificationEntityPath = Join-Path $entityDir "Notification.java"
@'
package com.inclusive.notificationservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Notification entity that represents any message sent to a user
 * across multiple channels (in-app, email, push).
 * Designed for accessibility, multi-tenant operation and analytics.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Tenant / institution identifier.
     * Enables multi-tenant separation.
     */
    private UUID tenantId;

    /**
     * Recipient user identifier.
     * This should match a user in auth-service.
     */
    private UUID userId;

    /**
     * Accessible title, short and descriptive.
     */
    private String title;

    /**
     * Human-readable, accessible message content.
     */
    @Column(length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    private NotificationPriority priority;

    /**
     * Extensible JSON payload that can be used for
     * AI metadata, tracking, assessment links, etc.
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    private boolean read;

    private boolean delivered;

    private boolean archived;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.priority == null) {
            this.priority = NotificationPriority.NORMAL;
        }
        if (this.channel == null) {
            this.channel = NotificationChannel.IN_APP;
        }
        if (this.type == null) {
            this.type = NotificationType.INFO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Notification() {
    }

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
'@ | Set-Content -Path $notificationEntityPath -Encoding UTF8
Write-Host "[OK]   Wrote: Notification.java"

$notificationTypePath = Join-Path $entityDir "NotificationType.java"
@'
package com.inclusive.notificationservice.entity;

/**
 * High-level semantic category of the notification.
 */
public enum NotificationType {
    SYSTEM,
    ALERT,
    INFO,
    WARNING,
    PROGRESS,
    ACHIEVEMENT,
    ASSESSMENT
}
'@ | Set-Content -Path $notificationTypePath -Encoding UTF8
Write-Host "[OK]   Wrote: NotificationType.java"

$notificationChannelPath = Join-Path $entityDir "NotificationChannel.java"
@'
package com.inclusive.notificationservice.entity;

/**
 * Delivery channel for the notification.
 */
public enum NotificationChannel {
    IN_APP,
    EMAIL,
    PUSH
}
'@ | Set-Content -Path $notificationChannelPath -Encoding UTF8
Write-Host "[OK]   Wrote: NotificationChannel.java"

$notificationPriorityPath = Join-Path $entityDir "NotificationPriority.java"
@'
package com.inclusive.notificationservice.entity;

/**
 * Priority level for notification delivery and UX.
 */
public enum NotificationPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}
'@ | Set-Content -Path $notificationPriorityPath -Encoding UTF8
Write-Host "[OK]   Wrote: NotificationPriority.java"

# 4. DTO

$notificationDtoPath = Join-Path $dtoDir "NotificationDTO.java"
@'
package com.inclusive.notificationservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO used to expose notification data through REST API
 * and to decouple external representation from persistence layer.
 */
public class NotificationDTO {

    private UUID id;

    @NotNull
    private UUID tenantId;

    @NotNull
    private UUID userId;

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 2000)
    private String message;

    private String type;

    private String channel;

    private String priority;

    /**
     * JSON string with extensible metadata (e.g., URLs, context, AI info).
     */
    private String metadata;

    private Boolean read;

    private Boolean delivered;

    private Boolean archived;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public NotificationDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
'@ | Set-Content -Path $notificationDtoPath -Encoding UTF8
Write-Host "[OK]   Wrote: NotificationDTO.java"

# 5. Mapper

$notificationMapperPath = Join-Path $mapperDir "NotificationMapper.java"
@'
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
'@ | Set-Content -Path $notificationMapperPath -Encoding UTF8
Write-Host "[OK]   Wrote: NotificationMapper.java"

# 6. Repository

$notificationRepositoryPath = Join-Path $repositoryDir "NotificationRepository.java"
@'
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
'@ | Set-Content -Path $notificationRepositoryPath -Encoding UTF8
Write-Host "[OK]   Wrote: NotificationRepository.java"

# 7. Service interface

$notificationServicePath = Join-Path $serviceDir "NotificationService.java"
@'
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
'@ | Set-Content -Path $notificationServicePath -Encoding UTF8
Write-Host "[OK]   Wrote: NotificationService.java"

# 8. Service implementation

$notificationServiceImplPath = Join-Path $serviceImplDir "NotificationServiceImpl.java"
@'
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
'@ | Set-Content -Path $notificationServiceImplPath -Encoding UTF8
Write-Host "[OK]   Wrote: NotificationServiceImpl.java"

# 9. REST Controller

$notificationControllerPath = Join-Path $controllerDir "NotificationController.java"
@'
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
'@ | Set-Content -Path $notificationControllerPath -Encoding UTF8
Write-Host "[OK]   Wrote: NotificationController.java"

Write-Host "==============================================="
Write-Host " NOTIFICATION-SERVICE FIX COMPLETADO"
Write-Host " Ahora ejecuta:"
Write-Host "   mvn clean package -pl notification-service -am"
Write-Host " y luego, si todo está OK:"
Write-Host "   mvn clean package -am"
Write-Host "==============================================="
