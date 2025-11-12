package com.inclusive.authservice.dto;

import com.inclusive.authservice.model.enums.AccountStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
    private Set<String> permissions;
    private AccountStatus accountStatus;
    private boolean emailVerified;
    private LocalDateTime createdAt;
}





