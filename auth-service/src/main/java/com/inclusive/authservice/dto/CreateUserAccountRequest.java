// Location: auth-service/src/main/java/com/inclusive/authservice/dto/CreateUserAccountRequest.java
package com.inclusive.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateUserAccountRequest {

    @NotBlank
    private String tenantId;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    // Getters & Setters
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

    public String getPassword() {
        return password;
    }
}
