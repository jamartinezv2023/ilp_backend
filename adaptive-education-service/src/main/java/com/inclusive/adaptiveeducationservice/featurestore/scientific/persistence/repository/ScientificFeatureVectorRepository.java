package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.repository;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ScientificFeatureVectorRepository
        extends JpaRepository<
        ScientificFeatureVectorEntity,
        String
        > {

    boolean existsByParticipantIdAndFeatureSetVersionAndFeatureCutoffAt(
            String participantId,
            String featureSetVersion,
            Instant featureCutoffAt
    );

    @EntityGraph(attributePaths = "items")
    Optional<ScientificFeatureVectorEntity>
    findByParticipantIdAndFeatureSetVersionAndFeatureCutoffAt(
            String participantId,
            String featureSetVersion,
            Instant featureCutoffAt
    );

    Optional<ScientificFeatureVectorEntity>
    findFirstByParticipantIdAndFeatureSetVersionAndStatusOrderByFeatureCutoffAtDescGeneratedAtDesc(
            String participantId,
            String featureSetVersion,
            ScientificFeatureVectorStatus status
    );

    List<ScientificFeatureVectorEntity>
    findByParticipantIdAndFeatureSetVersionOrderByFeatureCutoffAtDescGeneratedAtDesc(
            String participantId,
            String featureSetVersion
    );
}