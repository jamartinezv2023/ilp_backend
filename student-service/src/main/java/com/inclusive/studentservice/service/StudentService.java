package com.inclusive.studentservice.service;

import com.inclusive.studentservice.domain.Student;
import java.util.List;

public interface StudentService {
    List<Student> findAll();
    Student findById(Long id);
    Student save(Student student);
    void deleteById(Long id); // 👈 Nuevo método
}