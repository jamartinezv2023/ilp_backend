package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.repository;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureGenerationRunEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureGenerationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ScientificFeatureGenerationRunRepository
        extends JpaRepository<
        ScientificFeatureGenerationRunEntity,
        String
        > {

    Optional<ScientificFeatureGenerationRunEntity>
    findFirstByParticipantIdAndFeatureSetVersionOrderByStartedAtDesc(
            String participantId,
            String featureSetVersion
    );

    List<ScientificFeatureGenerationRunEntity>
    findByParticipantIdAndFeatureSetVersionAndFeatureCutoffAtOrderByStartedAtDesc(
            String participantId,
            String featureSetVersion,
            Instant featureCutoffAt
    );

    List<ScientificFeatureGenerationRunEntity>
    findByStatusOrderByStartedAtAsc(
            ScientificFeatureGenerationStatus status
    );
}