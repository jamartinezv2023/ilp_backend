package com.inclusive.authservice.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.authservice.model.UserMfa;
import com.inclusive.authservice.repository.UserMfaRepository;
import com.inclusive.authservice.service.UserMfaService;

@Service
public class UserMfaServiceImpl implements UserMfaService {

    @Autowired
    private UserMfaRepository repository;

    @Override
    public List<UserMfa> listAll() { return repository.findAll(); }

    @Override
    public UserMfa getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public UserMfa create(UserMfa entity) { return repository.save(entity); }

    @Override
    public UserMfa update(Long id, UserMfa entity) {
        Optional<UserMfa> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}