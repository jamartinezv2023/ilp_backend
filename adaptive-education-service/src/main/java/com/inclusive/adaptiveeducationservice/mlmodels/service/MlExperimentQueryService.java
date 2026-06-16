package com.inclusive.adaptiveeducationservice.mlmodels.service;

import com.inclusive.adaptiveeducationservice.mlmodels.dto.MlExperimentResponse;
import com.inclusive.adaptiveeducationservice.mlmodels.entity.MlExperimentEntity;
import com.inclusive.adaptiveeducationservice.mlmodels.repository.MlExperimentRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class MlExperimentQueryService {

    private final MlExperimentRepository repository;

    public MlExperimentQueryService(
            MlExperimentRepository repository
    ) {
        this.repository = repository;
    }

    public List<MlExperimentResponse> findAll() {

        return repository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                MlExperimentEntity::getCreatedAt
                        ).reversed()
                )
                .map(this::toResponse)
                .toList();
    }

    public List<MlExperimentResponse> findByAlgorithm(
            String algorithm
    ) {

        return repository
                .findByAlgorithmOrderByCreatedAtDesc(
                        algorithm
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MlExperimentResponse toResponse(
            MlExperimentEntity entity
    ) {

        return new MlExperimentResponse(
                entity.getId(),
                entity.getAlgorithm(),
                entity.getTarget(),
                entity.getTotalRows(),
                entity.getTrainRows(),
                entity.getValidationRows(),
                entity.getTestRows(),
                entity.getAccuracy(),
                entity.getPrecisionScore(),
                entity.getRecallScore(),
                entity.getF1Score(),
                entity.getFeatureImportanceJson(),
                entity.getCreatedAt()
        );
    }
}