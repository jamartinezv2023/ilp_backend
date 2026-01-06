package com.inclusive.authservice.service.authorization.impl;

import com.inclusive.authservice.entity.authorization.UserRole;
import com.inclusive.authservice.repository.authorization.UserRoleRepository;
import com.inclusive.authservice.service.authorization.UserRoleService;
import com.inclusive.authservice.tenant.TenantContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public void assignRoleToUser(UUID userId, UUID roleId) {

        UUID tenantId = UUID.fromString(TenantContext.getTenantId());

        if (userRoleRepository.existsByUserIdAndRoleIdAndTenantIdAndActiveTrue(
                userId, roleId, tenantId)) {
            return; // idempotente
        }

        UserRole userRole = new UserRole(userId, roleId, tenantId);
        userRoleRepository.save(userRole);
    }

    @Override
    public void removeRoleFromUser(UUID userId, UUID roleId) {

        UUID tenantId = UUID.fromString(TenantContext.getTenantId());

        UserRole userRole = userRoleRepository
                .findByUserIdAndRoleIdAndTenantIdAndActiveTrue(
                        userId, roleId, tenantId
                )
                .orElseThrow(() ->
                        new IllegalStateException("Role not assigned to user")
                );

        userRole.revoke();
        userRoleRepository.save(userRole);
    }
}
