package com.inclusive.authservice.mapper.authorization;

import com.inclusive.authservice.dto.authorization.domain.RolePermissionDTO;
import com.inclusive.authservice.entity.authorization.RolePermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RolePermissionMapper {

    @Mapping(source = "role.id", target = "roleId")
    @Mapping(source = "role.name", target = "roleName")
    @Mapping(source = "permission.id", target = "permissionId")
    @Mapping(source = "permission.code", target = "permissionCode")
    @Mapping(source = "permission.description", target = "permissionDescription")
    RolePermissionDTO toDto(RolePermission entity);
}