package com.inclusive.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.authservice.model.StudentBehaviorMetric;

@Repository
public interface StudentBehaviorMetricRepository extends JpaRepository<StudentBehaviorMetric, Long> {
}