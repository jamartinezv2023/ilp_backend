package com.inclusive.common.domain.student;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;

    public StudentServiceImpl(StudentRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Student> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Student> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Student create(Student student) {
        return repository.save(student);
    }

    @Override
    public Optional<Student> update(Long id, Student updated) {
        return repository.findById(id).map(existing -> {
            existing.updateFrom(updated);
            return repository.save(existing);
        });
    }

    @Override
    public void deactivate(Long id) {
        repository.findById(id).ifPresent(student -> {
            student.deactivate();
            repository.save(student);
        });
    }
}
