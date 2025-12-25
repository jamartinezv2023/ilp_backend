package com.inclusive.authservice.service.authorization.impl;

import com.inclusive.authservice.entity.authorization.Role;
import com.inclusive.authservice.entity.authorization.UserRole;
import com.inclusive.authservice.exception.BadRequestException;
import com.inclusive.authservice.exception.ResourceNotFoundException;
import com.inclusive.authservice.repository.authorization.RoleRepository;
import com.inclusive.authservice.repository.authorization.UserRoleRepository;
import com.inclusive.authservice.service.authorization.UserRoleService;
import com.inclusive.authservice.tenant.TenantContext;
import com.inclusive.authservice.validator.AuthorizationValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public UserRoleServiceImpl(
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository
    ) {
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void assignRoleToUser(UUID userId, UUID roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        UUID tenantId = TenantContext.getTenantIdAsUUID();

        // 🔐 Reglas de dominio
        AuthorizationValidator.validateTenant(tenantId, role.getTenantId());
        AuthorizationValidator.validateActiveRole(role);

        // Idempotencia
        if (userRoleRepository.existsByUserAndRole(userId, roleId)) {
            return;
        }

        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setTenantId(tenantId);
        userRole.setActive(true);
        userRole.setAssignedAt(LocalDateTime.now());

        userRoleRepository.save(userRole);
    }

    @Override
    public void removeRoleFromUser(UUID userId, UUID roleId) {

        userRoleRepository.findByUserAndRole(userId, roleId)
                .ifPresent(userRole -> {
                    userRole.setActive(false);
                    userRole.setRevokedAt(LocalDateTime.now());
                    userRoleRepository.save(userRole);
                });
    }
}