package com.inclusive.authservice.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.authservice.model.StudentEnvironment;
import com.inclusive.authservice.repository.StudentEnvironmentRepository;
import com.inclusive.authservice.service.StudentEnvironmentService;

@Service
public class StudentEnvironmentServiceImpl implements StudentEnvironmentService {

    @Autowired
    private StudentEnvironmentRepository repository;

    @Override
    public List<StudentEnvironment> listAll() { return repository.findAll(); }

    @Override
    public StudentEnvironment getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public StudentEnvironment create(StudentEnvironment entity) { return repository.save(entity); }

    @Override
    public StudentEnvironment update(Long id, StudentEnvironment entity) {
        Optional<StudentEnvironment> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}