// Location: auth-service/src/main/java/com/inclusive/authservice/dto/UserAccountDTO.java
package com.inclusive.authservice.dto;

import java.util.UUID;

public class UserAccountDTO {

    private UUID id;
    private String tenantId;
    private String email;
    private boolean enabled;

    // Getters & Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
