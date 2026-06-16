package com.inclusive.adaptiveeducationservice.assessment.repository;

import com.inclusive.adaptiveeducationservice.assessment.entity.KuderAssessmentResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KuderAssessmentRepository
        extends JpaRepository<KuderAssessmentResultEntity, String> {

    List<KuderAssessmentResultEntity> findByStudentIdOrderByCreatedAtDesc(
            String studentId
    );
}