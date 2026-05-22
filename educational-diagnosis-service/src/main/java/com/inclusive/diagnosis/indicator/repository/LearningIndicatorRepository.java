package com.inclusive.diagnosis.indicator.repository;

import com.inclusive.diagnosis.indicator.entity.LearningIndicator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LearningIndicatorRepository
        extends JpaRepository<LearningIndicator, UUID> {
}
