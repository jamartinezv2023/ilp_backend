package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.adapter;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorStatus;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.mapper.ScientificFeatureItemMapper;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.mapper.ScientificFeatureVectorMapper;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.repository.ScientificFeatureVectorRepository;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureVectorPersistencePort;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureVectorQueryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Component
@Transactional
public class JpaScientificFeatureVectorAdapter
        implements
        ScientificFeatureVectorPersistencePort,
        ScientificFeatureVectorQueryPort {

    private final ScientificFeatureVectorRepository repository;
    private final ScientificFeatureVectorMapper mapper;

    @Autowired
    public JpaScientificFeatureVectorAdapter(
            ScientificFeatureVectorRepository repository
    ) {
        this(
                repository,
                new ScientificFeatureVectorMapper(
                        new ScientificFeatureItemMapper()
                )
        );
    }

    JpaScientificFeatureVectorAdapter(
            ScientificFeatureVectorRepository repository,
            ScientificFeatureVectorMapper mapper
    ) {
        this.repository =
                Objects.requireNonNull(
                        repository,
                        "repository is required"
                );

        this.mapper =
                Objects.requireNonNull(
                        mapper,
                        "mapper is required"
                );
    }

    @Override
    public ScientificFeatureVector save(
            ScientificFeatureVector vector
    ) {
        Objects.requireNonNull(
                vector,
                "scientific feature vector is required"
        );

        ScientificFeatureVectorEntity entity =
                mapper.toEntity(vector);

        ScientificFeatureVectorEntity persisted =
                repository.save(entity);

        return mapper.toDomain(persisted);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(
            ParticipantId participantId,
            FeatureSetVersion featureSetVersion,
            Instant featureCutoffAt
    ) {
        requireQueryArguments(
                participantId,
                featureSetVersion,
                featureCutoffAt
        );

        return repository
                .existsByParticipantIdAndFeatureSetVersionAndFeatureCutoffAt(
                        participantId.value(),
                        featureSetVersion.value(),
                        featureCutoffAt
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScientificFeatureVector> findExact(
            ParticipantId participantId,
            FeatureSetVersion featureSetVersion,
            Instant featureCutoffAt
    ) {
        requireQueryArguments(
                participantId,
                featureSetVersion,
                featureCutoffAt
        );

        return repository
                .findByParticipantIdAndFeatureSetVersionAndFeatureCutoffAt(
                        participantId.value(),
                        featureSetVersion.value(),
                        featureCutoffAt
                )
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScientificFeatureVector>
    findLatestCompleted(
            ParticipantId participantId,
            FeatureSetVersion featureSetVersion
    ) {
        Objects.requireNonNull(
                participantId,
                "participantId is required"
        );

        Objects.requireNonNull(
                featureSetVersion,
                "featureSetVersion is required"
        );

        return repository
                .findFirstByParticipantIdAndFeatureSetVersionAndStatusOrderByFeatureCutoffAtDescGeneratedAtDesc(
                        participantId.value(),
                        featureSetVersion.value(),
                        ScientificFeatureVectorStatus.COMPLETED
                )
                .map(mapper::toDomain);
    }

    private void requireQueryArguments(
            ParticipantId participantId,
            FeatureSetVersion featureSetVersion,
            Instant featureCutoffAt
    ) {
        Objects.requireNonNull(
                participantId,
                "participantId is required"
        );

        Objects.requireNonNull(
                featureSetVersion,
                "featureSetVersion is required"
        );

        Objects.requireNonNull(
                featureCutoffAt,
                "featureCutoffAt is required"
        );
    }
}