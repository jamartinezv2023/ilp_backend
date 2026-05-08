// notification-service/src/main/java/com/inclusive/notificationservice/domain/notification/Notification.java
package com.inclusive.notificationservice.domain.notification;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue
    private UUID id;

    private Long studentId;

    private String message;

    private boolean read;

    private OffsetDateTime createdAt;

    protected Notification() {
        // JPA
    }

    public Notification(Long studentId, String message) {
        this.studentId = studentId;
        this.message = message;
        this.read = false;
        this.createdAt = OffsetDateTime.now();
    }

    public void markAsRead() {
        this.read = true;
    }

    public UUID getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return read;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
