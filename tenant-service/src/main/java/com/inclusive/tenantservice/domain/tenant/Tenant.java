package com.inclusive.tenantservice.domain.tenant;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "tenants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;
    private Boolean active;
    private String contactEmail;
    private String contactPhone;
    private String country;
    private String timezone;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() { 
        createdAt = Instant.now(); 
        updatedAt = Instant.now(); 
    }
    
    @PreUpdate
    protected void onUpdate() { 
        updatedAt = Instant.now(); 
    }
}