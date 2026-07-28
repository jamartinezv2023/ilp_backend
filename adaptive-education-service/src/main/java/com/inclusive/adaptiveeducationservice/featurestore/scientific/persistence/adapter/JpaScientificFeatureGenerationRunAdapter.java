package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.adapter;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureGenerationRun;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureGenerationRunId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureGenerationRunEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.mapper.ScientificFeatureGenerationRunMapper;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.repository.ScientificFeatureGenerationRunRepository;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.repository.ScientificFeatureVectorRepository;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureGenerationRunPersistencePort;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureGenerationRunQueryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Component
@Transactional
public class JpaScientificFeatureGenerationRunAdapter
        implements
        ScientificFeatureGenerationRunPersistencePort,
        ScientificFeatureGenerationRunQueryPort {

    private final ScientificFeatureGenerationRunRepository
            runRepository;

    private final ScientificFeatureVectorRepository
            vectorRepository;

    private final ScientificFeatureGenerationRunMapper
            mapper;

    @Autowired
    public JpaScientificFeatureGenerationRunAdapter(
            ScientificFeatureGenerationRunRepository runRepository,
            ScientificFeatureVectorRepository vectorRepository
    ) {
        this(
                runRepository,
                vectorRepository,
                new ScientificFeatureGenerationRunMapper()
        );
    }

    JpaScientificFeatureGenerationRunAdapter(
            ScientificFeatureGenerationRunRepository runRepository,
            ScientificFeatureVectorRepository vectorRepository,
            ScientificFeatureGenerationRunMapper mapper
    ) {
        this.runRepository =
                Objects.requireNonNull(
                        runRepository,
                        "runRepository is required"
                );

        this.vectorRepository =
                Objects.requireNonNull(
                        vectorRepository,
                        "vectorRepository is required"
                );

        this.mapper =
                Objects.requireNonNull(
                        mapper,
                        "mapper is required"
                );
    }

    @Override
    public ScientificFeatureGenerationRun save(
            ScientificFeatureGenerationRun run
    ) {
        Objects.requireNonNull(
                run,
                "scientific feature generation run is required"
        );

        ScientificFeatureVectorEntity vectorEntity =
                resolveVectorEntity(run);

        ScientificFeatureGenerationRunEntity entity =
                mapper.toEntity(
                        run,
                        vectorEntity
                );

        ScientificFeatureGenerationRunEntity persisted =
                runRepository.save(entity);

        return mapper.toDomain(persisted);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScientificFeatureGenerationRun> findById(
            ScientificFeatureGenerationRunId runId
    ) {
        Objects.requireNonNull(
                runId,
                "runId is required"
        );

        return runRepository
                .findById(runId.value())
                .map(mapper::toDomain);
    }

    private ScientificFeatureVectorEntity resolveVectorEntity(
            ScientificFeatureGenerationRun run
    ) {
        if (run.featureVectorId() == null) {
            return null;
        }

        return vectorRepository
                .findById(
                        run.featureVectorId().value()
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Scientific feature vector not found: "
                                        + run.featureVectorId().value()
                        )
                );
    }
}