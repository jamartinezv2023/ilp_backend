package com.inclusive.authservice.service.authorization;

import com.inclusive.authservice.dto.authorization.domain.RolePermissionDTO;

import java.util.List;
import java.util.UUID;

public interface RolePermissionService {

    void assignPermissionToRole(UUID roleId, UUID permissionId);

    void removePermissionFromRole(UUID roleId, UUID permissionId);

    List<RolePermissionDTO> getPermissionsByRole(UUID roleId);
}