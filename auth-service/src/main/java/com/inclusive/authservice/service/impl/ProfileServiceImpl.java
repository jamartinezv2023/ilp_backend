package com.inclusive.authservice.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.authservice.model.Profile;
import com.inclusive.authservice.repository.ProfileRepository;
import com.inclusive.authservice.service.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository repository;

    @Override
    public List<Profile> listAll() { return repository.findAll(); }

    @Override
    public Profile getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public Profile create(Profile entity) { return repository.save(entity); }

    @Override
    public Profile update(Long id, Profile entity) {
        Optional<Profile> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}