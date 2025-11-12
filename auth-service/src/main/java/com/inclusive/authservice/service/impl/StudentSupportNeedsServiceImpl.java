package com.inclusive.authservice.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.authservice.model.StudentSupportNeeds;
import com.inclusive.authservice.repository.StudentSupportNeedsRepository;
import com.inclusive.authservice.service.StudentSupportNeedsService;

@Service
public class StudentSupportNeedsServiceImpl implements StudentSupportNeedsService {

    @Autowired
    private StudentSupportNeedsRepository repository;

    @Override
    public List<StudentSupportNeeds> listAll() { return repository.findAll(); }

    @Override
    public StudentSupportNeeds getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public StudentSupportNeeds create(StudentSupportNeeds entity) { return repository.save(entity); }

    @Override
    public StudentSupportNeeds update(Long id, StudentSupportNeeds entity) {
        Optional<StudentSupportNeeds> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}