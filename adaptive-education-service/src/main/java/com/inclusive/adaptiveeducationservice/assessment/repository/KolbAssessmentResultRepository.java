package com.inclusive.adaptiveeducationservice.assessment.repository;

import com.inclusive.adaptiveeducationservice.assessment.entity.KolbAssessmentResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KolbAssessmentResultRepository
        extends JpaRepository<KolbAssessmentResultEntity, String> {

    List<KolbAssessmentResultEntity> findByStudentIdOrderByCreatedAtDesc(
            String studentId
    );
}