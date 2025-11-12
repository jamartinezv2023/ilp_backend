package com.inclusive.tenantservice.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.tenantservice.model.TenantServiceApplication;
import com.inclusive.tenantservice.repository.TenantServiceApplicationRepository;
import com.inclusive.tenantservice.service.TenantServiceApplicationService;

@Service
public class TenantServiceApplicationServiceImpl implements TenantServiceApplicationService {

    @Autowired
    private TenantServiceApplicationRepository repository;

    @Override
    public List<TenantServiceApplication> listAll() { return repository.findAll(); }

    @Override
    public TenantServiceApplication getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public TenantServiceApplication create(TenantServiceApplication entity) { return repository.save(entity); }

    @Override
    public TenantServiceApplication update(Long id, TenantServiceApplication entity) {
        Optional<TenantServiceApplication> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}