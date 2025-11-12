package com.inclusive.tenantservice.tenant.service;

import com.inclusive.tenantservice.tenant.entity.Tenant;
import com.inclusive.tenantservice.tenant.repository.TenantRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    public Optional<Tenant> findById(Long id) {
        return tenantRepository.findById(id);
    }

    public Tenant create(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    public Tenant update(Long id, Tenant tenantDetails) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id " + id));

        tenant.setName(tenantDetails.getName());
        tenant.setSchema(tenantDetails.getSchema());
        tenant.setDatabaseUrl(tenantDetails.getDatabaseUrl());
        tenant.setUsername(tenantDetails.getUsername());
        tenant.setPassword(tenantDetails.getPassword());
        tenant.setActive(tenantDetails.isActive());

        return tenantRepository.save(tenant);
    }

    public void delete(Long id) {
        tenantRepository.deleteById(id);
    }
}




