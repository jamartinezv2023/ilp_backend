package com.inclusive.authservice.mapper;

import com.inclusive.authservice.dto.UserAccountDTO;
import com.inclusive.authservice.entity.UserAccount;

/**
 * Mapper utility between UserAccount entity and UserAccountDTO.
 */
public final class UserAccountMapper {

    private UserAccountMapper() {
    }

    public static UserAccountDTO toDto(UserAccount entity) {
        if (entity == null) {
            return null;
        }
        UserAccountDTO dto = new UserAccountDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setRoles(entity.getRoles());
        dto.setEnabled(entity.isEnabled());
        dto.setLocked(entity.isLocked());
        dto.setTenantId(entity.getTenantId());
        dto.setMfaEnabled(entity.isMfaEnabled());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        // Never map passwordHash to password for output.
        return dto;
    }

    public static UserAccount toEntity(UserAccountDTO dto) {
        if (dto == null) {
            return null;
        }
        UserAccount entity = new UserAccount();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setRoles(dto.getRoles());
        entity.setEnabled(dto.isEnabled());
        entity.setLocked(dto.isLocked());
        entity.setTenantId(dto.getTenantId());
        entity.setMfaEnabled(dto.isMfaEnabled());
        // Password hashing should be done in the service layer.
        return entity;
    }

    public static void updateEntityFromDto(UserAccountDTO dto, UserAccount entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setRoles(dto.getRoles());
        entity.setEnabled(dto.isEnabled());
        entity.setLocked(dto.isLocked());
        entity.setTenantId(dto.getTenantId());
        entity.setMfaEnabled(dto.isMfaEnabled());
    }
}
