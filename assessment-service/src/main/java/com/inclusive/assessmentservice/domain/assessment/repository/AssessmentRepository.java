package com.inclusive.assessmentservice.domain.assessment.repository;

import com.inclusive.assessmentservice.domain.assessment.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findByStudentId(Long studentId);
}
