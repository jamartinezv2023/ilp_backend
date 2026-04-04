package com.inclusive.adaptiveeducationservice.domain.adaptation.repository;

import com.inclusive.adaptiveeducationservice.domain.adaptation.LearningRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningRecommendationRepository extends JpaRepository<LearningRecommendation, Long> {
    List<LearningRecommendation> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
