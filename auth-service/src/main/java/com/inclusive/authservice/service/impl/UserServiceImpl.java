package com.inclusive.authservice.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.authservice.model.User;
import com.inclusive.authservice.repository.UserRepository;
import com.inclusive.authservice.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Override
    public List<User> listAll() { return repository.findAll(); }

    @Override
    public User getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public User create(User entity) { return repository.save(entity); }

    @Override
    public User update(Long id, User entity) {
        Optional<User> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}