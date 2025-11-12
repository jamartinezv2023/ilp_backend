package com.inclusive.authservice.service;

import java.util.List;
import com.inclusive.authservice.model.StudentBehaviorMetric;

public interface StudentBehaviorMetricService {
    List<StudentBehaviorMetric> listAll();
    StudentBehaviorMetric getById(Long id);
    StudentBehaviorMetric create(StudentBehaviorMetric entity);
    StudentBehaviorMetric update(Long id, StudentBehaviorMetric entity);
    void delete(Long id);
}