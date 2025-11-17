package com.inclusive.authservice.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.authservice.model.StudentLearningProfile;
    import com.inclusive.authservice.repository.StudentLearningProfileRepository;
import com.inclusive.authservice.service.StudentLearningProfileService;

@Service
public class StudentLearningProfileServiceImpl implements StudentLearningProfileService {

    @Autowired
    private StudentLearningProfileRepository repository;

    @Override
    public List<StudentLearningProfile> listAll() { return repository.findAll(); }

    @Override
    public StudentLearningProfile getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public StudentLearningProfile create(StudentLearningProfile entity) { return repository.save(entity); }

    @Override
    public StudentLearningProfile update(Long id, StudentLearningProfile entity) {
        Optional<StudentLearningProfile> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}