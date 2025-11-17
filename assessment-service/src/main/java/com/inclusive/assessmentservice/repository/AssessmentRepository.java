package com.inclusive.assessmentservice.repository;

import com.inclusive.assessmentservice.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {}