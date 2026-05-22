package com.inclusive.diagnosis.dua.repository;

import com.inclusive.diagnosis.dua.entity.DuaRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DuaRecommendationRepository
        extends JpaRepository<DuaRecommendation, UUID> {
}
