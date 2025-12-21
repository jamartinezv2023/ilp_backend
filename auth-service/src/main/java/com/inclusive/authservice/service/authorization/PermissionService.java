package com.inclusive.authservice.service.authorization;

import com.inclusive.authservice.entity.authorization.Permission;

import java.util.List;
import java.util.UUID;

public interface PermissionService {

    Permission createPermission(String code, String description);

    List<Permission> getAllPermissions();

    void deletePermission(UUID permissionId);
}