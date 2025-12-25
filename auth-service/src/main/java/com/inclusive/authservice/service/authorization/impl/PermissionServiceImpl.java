package com.inclusive.authservice.service.authorization.impl;

import com.inclusive.authservice.entity.authorization.Permission;
import com.inclusive.authservice.exception.BadRequestException;
import com.inclusive.authservice.exception.ResourceNotFoundException;
import com.inclusive.authservice.repository.authorization.PermissionRepository;
import com.inclusive.authservice.service.authorization.PermissionService;
import com.inclusive.authservice.tenant.TenantContext;
import com.inclusive.authservice.validator.AuthorizationValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository repository;

    public PermissionServiceImpl(PermissionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Permission createPermission(String code, String description) {

        UUID tenantId = TenantContext.getTenantIdAsUUID();

        if (repository.existsByTenantIdAndCodeIgnoreCase(tenantId, code)) {
            throw new BadRequestException("Permission already exists for this tenant: " + code);
        }

        Permission permission = new Permission();
        permission.setTenantId(tenantId);
        permission.setCode(code);
        permission.setDescription(description);

        return repository.save(permission);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return repository.findAllByTenantId(TenantContext.getTenantIdAsUUID());
    }

    @Override
    public void deletePermission(UUID permissionId) {

        Permission permission = repository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        AuthorizationValidator.validateTenant(
                TenantContext.getTenantIdAsUUID(),
                permission.getTenantId()
        );

        repository.delete(permission);
    }
}