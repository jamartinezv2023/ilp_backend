package com.inclusive.adaptiveeducationservice.assessmentdefinition.repository;

import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentDefinitionRepository
        extends JpaRepository<AssessmentDefinitionEntity, String> {

    Optional<AssessmentDefinitionEntity> findByCode(String code);

    Optional<AssessmentDefinitionEntity> findByCodeAndActiveTrue(
            String code
    );

    boolean existsByCode(String code);

    List<AssessmentDefinitionEntity> findAllByActiveTrueOrderByCodeAsc();
}