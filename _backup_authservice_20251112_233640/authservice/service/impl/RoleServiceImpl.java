package com.inclusive.authservice.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.authservice.model.Role;
    import com.inclusive.authservice.repository.RoleRepository;
import com.inclusive.authservice.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository repository;

    @Override
    public List<Role> listAll() { return repository.findAll(); }

    @Override
    public Role getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public Role create(Role entity) { return repository.save(entity); }

    @Override
    public Role update(Long id, Role entity) {
        Optional<Role> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}