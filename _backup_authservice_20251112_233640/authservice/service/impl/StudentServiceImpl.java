package com.inclusive.authservice.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.authservice.model.Student;
    import com.inclusive.authservice.repository.StudentRepository;
import com.inclusive.authservice.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository repository;

    @Override
    public List<Student> listAll() { return repository.findAll(); }

    @Override
    public Student getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public Student create(Student entity) { return repository.save(entity); }

    @Override
    public Student update(Long id, Student entity) {
        Optional<Student> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}