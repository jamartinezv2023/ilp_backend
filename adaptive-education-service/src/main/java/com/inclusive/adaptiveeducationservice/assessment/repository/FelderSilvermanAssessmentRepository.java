package com.inclusive.adaptiveeducationservice.assessment.repository;

import com.inclusive.adaptiveeducationservice.assessment.entity.FelderSilvermanAssessmentResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FelderSilvermanAssessmentRepository
        extends JpaRepository<FelderSilvermanAssessmentResultEntity, String> {

    List<FelderSilvermanAssessmentResultEntity>
    findByStudentIdOrderByCreatedAtDesc(String studentId);
}