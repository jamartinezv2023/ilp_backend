package com.inclusive.authservice.service.authorization;

import com.inclusive.authservice.entity.authorization.Permission;
import com.inclusive.authservice.entity.authorization.Role;
import com.inclusive.authservice.entity.authorization.RolePermission;

import java.util.List;
import java.util.UUID;

public interface RolePermissionService {

    RolePermission assignPermission(Role role, Permission permission);

    List<Permission> getPermissionsByRole(UUID roleId);
}
