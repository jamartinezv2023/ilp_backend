package com.inclusive.diagnosis.indicator.repository;

import com.inclusive.diagnosis.indicator.entity.LearningIndicator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LearningIndicatorRepository
        extends JpaRepository<LearningIndicator, UUID> {

    List<LearningIndicator> findByStudentProfileId(
            UUID studentProfileId
    );
}
