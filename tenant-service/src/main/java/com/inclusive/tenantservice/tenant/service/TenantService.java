package com.inclusive.tenantservice.tenant.service;

import com.inclusive.tenantservice.tenant.entity.Tenant;
import com.inclusive.tenantservice.tenant.exception.TenantNotFoundException;
import com.inclusive.tenantservice.tenant.repository.TenantRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Tenant getTenantById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException("Tenant con ID " + id + " no encontrado"));
    }

    public Tenant getTenantByName(String name) {
        return tenantRepository.findByName(name)
                .orElseThrow(() -> new TenantNotFoundException("Tenant con nombre '" + name + "' no encontrado"));
    }

    public Tenant createTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    public Tenant updateTenant(Long id, Tenant updatedTenant) {
        Tenant existing = getTenantById(id);
        existing.setName(updatedTenant.getName());
        existing.setSchema(updatedTenant.getSchema());
        existing.setDatabaseUrl(updatedTenant.getDatabaseUrl());
        existing.setUsername(updatedTenant.getUsername());
        existing.setPassword(updatedTenant.getPassword());
        existing.setActive(updatedTenant.isActive());
        return tenantRepository.save(existing);
    }

    public void deleteTenant(Long id) {
        Tenant tenant = getTenantById(id);
        tenantRepository.delete(tenant);
    }
}




