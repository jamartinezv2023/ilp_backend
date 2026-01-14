// Location: auth-service/src/main/java/com/inclusive/authservice/service/authorization/impl/RoleServiceImpl.java
package com.inclusive.authservice.service.authorization.impl;

import com.inclusive.authservice.entity.authorization.Role;
import com.inclusive.authservice.repository.authorization.RoleRepository;
import com.inclusive.authservice.service.authorization.RoleService;
import com.inclusive.authservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;

    @Override
    public Role create(Role role) {
        role.setTenantId(TenantContext.getTenantId());
        return repository.save(role);
    }

    @Override
    public List<Role> findAll() {
        return repository.findAllByTenantIdAndActiveTrueAndDeletedAtIsNull(
                TenantContext.getTenantId()
        );
    }

    @Override
    public Role findById(UUID roleId) {
        Role role = repository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        if (!role.getTenantId().equals(TenantContext.getTenantId())) {
            throw new SecurityException("Cross-tenant access denied");
        }

        return role;
    }

    @Override
    public List<Role> getActiveRoles() {
        return findAll();
    }

    @Override
    public void deactivate(UUID roleId) {
        Role role = findById(roleId);
        role.deactivate();
        repository.save(role);
    }
}
