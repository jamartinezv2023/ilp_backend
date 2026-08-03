package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentSubmissionContextEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AssessmentSubmissionContextRepository
        extends JpaRepository<
        AssessmentSubmissionContextEntity,
        String
        > {

    boolean existsByAdministrationId(
            String administrationId
    );

    Optional<AssessmentSubmissionContextEntity>
    findByAdministrationId(
            String administrationId
    );

    List<AssessmentSubmissionContextEntity>
    findByAdministrationIdIn(
            Collection<String> administrationIds
    );}