package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentScientificResultEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentScientificResultRepository
        extends JpaRepository<
        AssessmentScientificResultEntity,
        String
        > {

    boolean existsByAdministrationId(
            String administrationId
    );

    @EntityGraph(
            attributePaths = {
                    "scores",
                    "interpretations"
            }
    )
    Optional<AssessmentScientificResultEntity>
    findByAdministrationId(
            String administrationId
    );

    List<AssessmentScientificResultEntity>
    findByParticipantIdOrderByCalculatedAtDesc(
            String participantId
    );

    List<AssessmentScientificResultEntity>
    findByParticipantIdAndAssessmentCodeOrderByCalculatedAtDesc(
            String participantId,
            String assessmentCode
    );

    @EntityGraph(
            attributePaths = {
                    "scores",
                    "interpretations"
            }
    )
    List<AssessmentScientificResultEntity>
    findByParticipantIdOrderBySubmittedAtDescAdministrationIdAsc(
            String participantId
    );}