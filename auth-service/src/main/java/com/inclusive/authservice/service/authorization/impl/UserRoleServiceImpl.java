// Location: auth-service/src/main/java/com/inclusive/authservice/service/authorization/impl/UserRoleServiceImpl.java
package com.inclusive.authservice.service.authorization.impl;

import com.inclusive.authservice.entity.authorization.UserRole;
import com.inclusive.authservice.repository.authorization.UserRoleRepository;
import com.inclusive.authservice.service.authorization.UserRoleService;
import com.inclusive.authservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository repository;

    @Override
    public UserRole assignRole(UUID userId, UUID roleId) {

        UUID tenantId = TenantContext.getTenantId();

        return repository.save(new UserRole(userId, roleId, tenantId));
    }

    @Override
    public void revokeRole(UUID userRoleId) {

        UserRole userRole = repository.findById(userRoleId)
                .orElseThrow(() -> new IllegalArgumentException("UserRole not found"));

        if (!userRole.getTenantId().equals(TenantContext.getTenantId())) {
            throw new SecurityException("Cross-tenant revoke denied");
        }

        userRole.revoke();
        repository.save(userRole);
    }
}
