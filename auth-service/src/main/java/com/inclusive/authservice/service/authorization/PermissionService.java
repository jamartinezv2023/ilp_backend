package com.inclusive.authservice.service.authorization;

import com.inclusive.authservice.entity.authorization.Permission;

import java.util.List;
import java.util.UUID;

public interface PermissionService {

    Permission create(Permission permission);

    List<Permission> findAll();

    Permission findByCode(String code);

    void deletePermission(UUID permissionId);
}
