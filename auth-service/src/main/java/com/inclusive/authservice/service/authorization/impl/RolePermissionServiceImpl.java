// Location: auth-service/src/main/java/com/inclusive/authservice/service/authorization/impl/RolePermissionServiceImpl.java
package com.inclusive.authservice.service.authorization.impl;

import com.inclusive.authservice.dto.authorization.RolePermissionDTO;
import com.inclusive.authservice.entity.authorization.Permission;
import com.inclusive.authservice.entity.authorization.Role;
import com.inclusive.authservice.entity.authorization.RolePermission;
import com.inclusive.authservice.repository.authorization.PermissionRepository;
import com.inclusive.authservice.repository.authorization.RolePermissionRepository;
import com.inclusive.authservice.repository.authorization.RoleRepository;
import com.inclusive.authservice.service.authorization.RolePermissionService;
import com.inclusive.authservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionRepository repository;

    @Override
    public RolePermission assignPermission(Role role, Permission permission) {

        UUID tenantId = TenantContext.getTenantId();

        if (!role.getTenantId().equals(tenantId)
                || !permission.getTenantId().equals(tenantId)) {
            throw new SecurityException("Cross-tenant assignment denied");
        }

        RolePermission rp = new RolePermission();
        rp.setRole(role);
        rp.setPermission(permission);

        return repository.save(rp);
    }

    @Override
    public List<Permission> getPermissionsByRole(UUID roleId) {
        return repository.findAllByRoleIdWithPermission(roleId)
                .stream()
                .map(RolePermission::getPermission)
                .toList();
    }
}
