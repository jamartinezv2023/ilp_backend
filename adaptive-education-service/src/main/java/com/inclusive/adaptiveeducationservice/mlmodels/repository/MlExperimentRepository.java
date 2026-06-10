package com.inclusive.adaptiveeducationservice.mlmodels.repository;

import com.inclusive.adaptiveeducationservice.mlmodels.entity.MlExperimentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MlExperimentRepository
        extends JpaRepository<MlExperimentEntity, String> {

    List<MlExperimentEntity> findByAlgorithmOrderByCreatedAtDesc(
            String algorithm
    );
}