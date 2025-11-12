package com.inclusive.authservice.service;

import java.util.List;
import com.inclusive.authservice.model.Student;

public interface StudentService {
    List<Student> listAll();
    Student getById(Long id);
    Student create(Student entity);
    Student update(Long id, Student entity);
    void delete(Long id);
}