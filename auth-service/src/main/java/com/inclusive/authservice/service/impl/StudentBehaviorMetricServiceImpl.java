package com.inclusive.authservice.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.authservice.model.StudentBehaviorMetric;
import com.inclusive.authservice.repository.StudentBehaviorMetricRepository;
import com.inclusive.authservice.service.StudentBehaviorMetricService;

@Service
public class StudentBehaviorMetricServiceImpl implements StudentBehaviorMetricService {

    @Autowired
    private StudentBehaviorMetricRepository repository;

    @Override
    public List<StudentBehaviorMetric> listAll() { return repository.findAll(); }

    @Override
    public StudentBehaviorMetric getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public StudentBehaviorMetric create(StudentBehaviorMetric entity) { return repository.save(entity); }

    @Override
    public StudentBehaviorMetric update(Long id, StudentBehaviorMetric entity) {
        Optional<StudentBehaviorMetric> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}