package com.inclusive.authservice.service;

import java.util.List;
import com.inclusive.authservice.model.StudentLearningProfile;

public interface StudentLearningProfileService {
    List<StudentLearningProfile> listAll();
    StudentLearningProfile getById(Long id);
    StudentLearningProfile create(StudentLearningProfile entity);
    StudentLearningProfile update(Long id, StudentLearningProfile entity);
    void delete(Long id);
}