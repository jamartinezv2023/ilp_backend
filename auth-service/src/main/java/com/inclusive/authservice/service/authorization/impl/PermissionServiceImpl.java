// Location: auth-service/src/main/java/com/inclusive/authservice/service/authorization/impl/PermissionServiceImpl.java
package com.inclusive.authservice.service.authorization.impl;

import com.inclusive.authservice.entity.authorization.Permission;
import com.inclusive.authservice.repository.authorization.PermissionRepository;
import com.inclusive.authservice.service.authorization.PermissionService;
import com.inclusive.authservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository repository;

    @Override
    public Permission create(Permission permission) {
        permission.setTenantId(TenantContext.getTenantId());
        return repository.save(permission);
    }

    @Override
    public List<Permission> findAll() {
        return repository.findAllByTenantId(TenantContext.getTenantId());
    }

    @Override
    public Permission findByCode(String code) {
        return repository
                .findByTenantIdAndCodeIgnoreCase(TenantContext.getTenantId(), code)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));
    }

    @Override
    public void deletePermission(UUID permissionId) {
        repository.deleteById(permissionId);
    }
}
