package com.inclusive.tenantservice.tenant.entity.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.tenantservice.tenant.entity.model.Tenant;
import com.inclusive.tenantservice.tenant.entity.repository.TenantRepository;
import com.inclusive.tenantservice.tenant.entity.service.TenantService;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepository repository;

    @Override
    public List<Tenant> listAll() { return repository.findAll(); }

    @Override
    public Tenant getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public Tenant create(Tenant entity) { return repository.save(entity); }

    @Override
    public Tenant update(Long id, Tenant entity) {
        Optional<Tenant> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}