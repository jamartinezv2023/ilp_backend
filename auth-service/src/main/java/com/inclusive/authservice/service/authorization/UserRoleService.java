package com.inclusive.authservice.service.authorization;

import com.inclusive.authservice.entity.authorization.UserRole;

import java.util.UUID;

public interface UserRoleService {

    UserRole assignRole(UUID userId, UUID roleId);

    void revokeRole(UUID userRoleId);
}
