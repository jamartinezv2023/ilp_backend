package com.inclusive.tenantservice.mapper;

import com.inclusive.tenantservice.dto.TenantDTO;
import com.inclusive.tenantservice.entity.Tenant;

public final class TenantMapper {

    private TenantMapper() {
    }

    public static TenantDTO toDto(Tenant entity) {
        if (entity == null) {
            return null;
        }
        TenantDTO dto = new TenantDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setActive(entity.getActive());
        dto.setContactEmail(entity.getContactEmail());
        dto.setContactPhone(entity.getContactPhone());
        dto.setCountry(entity.getCountry());
        dto.setTimezone(entity.getTimezone());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public static void updateEntity(Tenant entity, TenantDTO dto) {
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.getActive());
        entity.setContactEmail(dto.getContactEmail());
        entity.setContactPhone(dto.getContactPhone());
        entity.setCountry(dto.getCountry());
        entity.setTimezone(dto.getTimezone());
    }

    public static Tenant toNewEntity(TenantDTO dto) {
        Tenant entity = new Tenant();
        updateEntity(entity, dto);
        return entity;
    }
}