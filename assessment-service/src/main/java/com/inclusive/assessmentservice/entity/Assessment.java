package com.inclusive.assessmentservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String type;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setType(String type) { this.type = type; }
}