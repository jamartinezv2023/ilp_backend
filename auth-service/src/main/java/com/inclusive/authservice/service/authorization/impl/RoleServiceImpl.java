package com.inclusive.authservice.service.authorization.impl;

import com.inclusive.authservice.entity.authorization.Role;
import com.inclusive.authservice.exception.BadRequestException;
import com.inclusive.authservice.exception.ResourceNotFoundException;
import com.inclusive.authservice.repository.authorization.RoleRepository;
import com.inclusive.authservice.service.authorization.RoleService;
import com.inclusive.authservice.tenant.TenantContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;

    public RoleServiceImpl(RoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Role create(String name, String description) {
        UUID tenantId = TenantContext.getTenantIdAsUUID();

        if (repository.existsByTenantIdAndNameIgnoreCase(tenantId, name)) {
            throw new BadRequestException("Role already exists for this tenant");
        }

        Role role = new Role();
        role.setTenantId(tenantId);
        role.setName(name);
        role.setDescription(description);

        return repository.save(role);
    }

    @Override
    public List<Role> getActiveRoles() {
        return repository.findAllByTenantIdAndActiveTrueAndDeletedAtIsNull(
                TenantContext.getTenantIdAsUUID()
        );
    }

    @Override
    public void deactivate(UUID roleId) {
        Role role = repository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        if (!role.getTenantId().equals(TenantContext.getTenantIdAsUUID())) {
            throw new BadRequestException("Role does not belong to tenant");
        }

        if (!role.isActive()) {
            return; // idempotente
        }

        role.deactivate();
        repository.save(role);
    }
}