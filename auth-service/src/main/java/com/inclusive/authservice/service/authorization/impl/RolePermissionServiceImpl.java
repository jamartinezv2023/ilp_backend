package com.inclusive.authservice.service.authorization.impl;

import com.inclusive.authservice.dto.authorization.domain.RolePermissionDTO;
import com.inclusive.authservice.entity.authorization.Permission;
import com.inclusive.authservice.entity.authorization.Role;
import com.inclusive.authservice.entity.authorization.RolePermission;
import com.inclusive.authservice.exception.ResourceNotFoundException;
import com.inclusive.authservice.mapper.authorization.RolePermissionMapper;
import com.inclusive.authservice.repository.authorization.PermissionRepository;
import com.inclusive.authservice.repository.authorization.RolePermissionRepository;
import com.inclusive.authservice.repository.authorization.RoleRepository;
import com.inclusive.authservice.service.authorization.RolePermissionService;
import com.inclusive.authservice.tenant.TenantContext;
import com.inclusive.authservice.validator.AuthorizationValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RolePermissionMapper rolePermissionMapper;

    public RolePermissionServiceImpl(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository,
            RolePermissionMapper rolePermissionMapper
    ) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public void assignPermissionToRole(UUID roleId, UUID permissionId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        UUID tenantId = TenantContext.getTenantIdAsUUID();

        AuthorizationValidator.validateTenant(tenantId, role.getTenantId());
        AuthorizationValidator.validateTenant(tenantId, permission.getTenantId());
        AuthorizationValidator.validateActiveRole(role);

        if (rolePermissionRepository.existsByRole_IdAndPermission_Id(roleId, permissionId)) {
            return;
        }

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);

        rolePermissionRepository.save(rolePermission);
    }

    @Override
    public void removePermissionFromRole(UUID roleId, UUID permissionId) {
        rolePermissionRepository
                .findByRole_IdAndPermission_Id(roleId, permissionId)
                .ifPresent(rolePermissionRepository::delete);
    }

    @Override
    public List<RolePermissionDTO> getPermissionsByRole(UUID roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        UUID tenantId = TenantContext.getTenantIdAsUUID();

        AuthorizationValidator.validateTenant(tenantId, role.getTenantId());
        AuthorizationValidator.validateActiveRole(role);

        return rolePermissionRepository
                .findAllByRoleIdWithPermission(roleId)
                .stream()
                .map(rolePermissionMapper::toDto)
                .collect(Collectors.toList());
    }
}