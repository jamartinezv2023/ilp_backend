package com.inclusive.adaptiveeducationservice.intervention.repository;

import com.inclusive.adaptiveeducationservice.intervention.entity.EducationalInterventionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationalInterventionRepository
        extends JpaRepository<EducationalInterventionEntity, String> {

    List<EducationalInterventionEntity> findByStudentIdOrderByCreatedAtDesc(
            String studentId
    );
}