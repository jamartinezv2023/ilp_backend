package com.inclusive.studentservice.service;

import com.inclusive.studentservice.domain.Student;
import com.inclusive.studentservice.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service("studentServiceLocal")
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        // Casteo seguro y directo
        return (List<Student>) studentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Student save(Student student) {
        // Aquí podrías añadir lógica extra antes de guardar
        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        studentRepository.findById(id).ifPresent(student -> {
            student.deactivate(); // Usa el método de la entidad
            studentRepository.save(student);
        });
    }
}