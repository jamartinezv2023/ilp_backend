package com.inclusive.authservice.mapper.authorization;

import com.inclusive.authservice.dto.authorization.domain.PermissionDTO;
import com.inclusive.authservice.entity.authorization.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(target = "tenantId", ignore = true)
    Permission toEntity(PermissionDTO dto);

    PermissionDTO toDto(Permission entity);
}