// common-libs/src/main/java/com/inclusive/common/dto/StudentDTO.java
package com.inclusive.common.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO compartido que representa la informaciÃ³n esencial del estudiante.
 * Este objeto se intercambia entre microservicios sin acoplamiento con JPA.
 */
public class StudentDTO {
    private Long id;
    private UUID tenantId;
    private String fullName;
    private String gender;
    private String location;
    private String accountStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}




