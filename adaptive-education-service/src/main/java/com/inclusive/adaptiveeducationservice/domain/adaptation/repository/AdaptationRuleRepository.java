package com.inclusive.adaptiveeducationservice.domain.adaptation.repository;

import com.inclusive.adaptiveeducationservice.domain.adaptation.AdaptationRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdaptationRuleRepository extends JpaRepository<AdaptationRule, Long> {
    List<AdaptationRule> findByTenantIdAndEnabledTrue(String tenantId);
}
