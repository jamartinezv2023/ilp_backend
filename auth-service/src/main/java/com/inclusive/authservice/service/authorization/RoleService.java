package com.inclusive.authservice.service.authorization;

import com.inclusive.authservice.entity.authorization.Role;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    Role create(Role role);

    List<Role> findAll();

    Role findById(UUID roleId);

    List<Role> getActiveRoles();

    void deactivate(UUID roleId);
}
