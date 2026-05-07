package com.inclusive.adaptiveeducationservice.domain.adaptation.repository;

import com.inclusive.adaptiveeducationservice.domain.adaptation.AdaptationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdaptationRuleRepository extends JpaRepository<AdaptationRule, Long> {
    Optional<AdaptationRule> findByStyleKeyAndStyleValue(String styleKey, String styleValue);
}
