package com.inclusive.adaptiveeducationservice.adaptive.repository;

import com.inclusive.adaptiveeducationservice.adaptive.entity.AdaptiveLearningPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdaptiveLearningPlanRepository
        extends JpaRepository<AdaptiveLearningPlanEntity, String> {

    List<AdaptiveLearningPlanEntity> findByStudentIdOrderByCreatedAtDesc(
            String studentId
    );
}