package com.inclusive.adaptiveeducationservice.domain.adaptation.repository;

import com.inclusive.adaptiveeducationservice.domain.adaptation.LearningRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LearningRecommendationRepository extends JpaRepository<LearningRecommendation, Long> {
    List<LearningRecommendation> findByStudentId(Long studentId);
}
