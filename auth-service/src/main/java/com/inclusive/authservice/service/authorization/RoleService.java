package com.inclusive.authservice.service.authorization;

import com.inclusive.authservice.entity.authorization.Role;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    Role create(String name, String description);

    List<Role> getActiveRoles();

    void deactivate(UUID roleId);
}