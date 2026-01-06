package com.inclusive.authservice.mapper.authorization;

import com.inclusive.authservice.dto.authorization.domain.RoleDTO;
import com.inclusive.authservice.entity.authorization.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "deletedAt", ignore = true)
    Role toEntity(RoleDTO dto);

    RoleDTO toDto(Role entity);
}