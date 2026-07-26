package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.repository;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScientificFeatureItemRepository
        extends JpaRepository<
        ScientificFeatureItemEntity,
        String
        > {

    List<ScientificFeatureItemEntity>
    findByFeatureVector_IdOrderByFeatureCodeAsc(
            String featureVectorId
    );

    boolean existsByFeatureVector_IdAndFeatureCode(
            String featureVectorId,
            String featureCode
    );
}