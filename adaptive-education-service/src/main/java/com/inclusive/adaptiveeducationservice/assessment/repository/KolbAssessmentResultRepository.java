package com.inclusive.adaptiveeducationservice.assessment.repository;

import com.inclusive.adaptiveeducationservice.assessment.entity.KolbAssessmentResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface KolbAssessmentResultRepository
        extends JpaRepository<KolbAssessmentResultEntity, String> {

    List<KolbAssessmentResultEntity> findByStudentIdOrderByCreatedAtDesc(
            String studentId
    );

    Optional<KolbAssessmentResultEntity>
            findFirstByStudentIdAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
                    String studentId,
                    Instant createdAt
            );
}