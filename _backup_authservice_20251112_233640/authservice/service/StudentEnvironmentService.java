package com.inclusive.authservice.service;

import java.util.List;
import com.inclusive.authservice.model.StudentEnvironment;

public interface StudentEnvironmentService {
    List<StudentEnvironment> listAll();
    StudentEnvironment getById(Long id);
    StudentEnvironment create(StudentEnvironment entity);
    StudentEnvironment update(Long id, StudentEnvironment entity);
    void delete(Long id);
}