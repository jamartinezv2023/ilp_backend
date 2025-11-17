package com.inclusive.authservice.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.authservice.model.Permission;
    import com.inclusive.authservice.repository.PermissionRepository;
import com.inclusive.authservice.service.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository repository;

    @Override
    public List<Permission> listAll() { return repository.findAll(); }

    @Override
    public Permission getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public Permission create(Permission entity) { return repository.save(entity); }

    @Override
    public Permission update(Long id, Permission entity) {
        Optional<Permission> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}