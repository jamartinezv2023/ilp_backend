package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.mapper;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureDataType;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureItemEntity;

import java.util.Objects;

public final class ScientificFeatureItemMapper {

    public ScientificFeatureItemEntity toEntity(
            ScientificFeatureItem domain
    ) {
        Objects.requireNonNull(
                domain,
                "domain feature item is required"
        );

        FeatureValue value = domain.value();

        if (value instanceof FeatureValue.NumericValue numericValue) {
            return ScientificFeatureItemEntity.numeric(
                    domain.id(),
                    domain.featureCode().value(),
                    numericValue.value(),
                    domain.sourceAssessmentCode(),
                    domain.sourceAdministrationId()
            );
        }

        if (value instanceof FeatureValue.TextValue textValue) {
            return ScientificFeatureItemEntity.text(
                    domain.id(),
                    domain.featureCode().value(),
                    textValue.value(),
                    domain.sourceAssessmentCode(),
                    domain.sourceAdministrationId()
            );
        }

        if (value instanceof FeatureValue.BooleanValue booleanValue) {
            return ScientificFeatureItemEntity.bool(
                    domain.id(),
                    domain.featureCode().value(),
                    booleanValue.value(),
                    domain.sourceAssessmentCode(),
                    domain.sourceAdministrationId()
            );
        }

        throw new IllegalArgumentException(
                "Unsupported scientific feature value type: "
                        + value.getClass().getName()
        );
    }

    public ScientificFeatureItem toDomain(
            ScientificFeatureItemEntity entity
    ) {
        Objects.requireNonNull(
                entity,
                "scientific feature item entity is required"
        );

        return new ScientificFeatureItem(
                entity.getId(),
                new FeatureCode(
                        entity.getFeatureCode()
                ),
                toDomainValue(entity),
                entity.getSourceAssessmentCode(),
                entity.getSourceAdministrationId()
        );
    }

    private FeatureValue toDomainValue(
            ScientificFeatureItemEntity entity
    ) {
        ScientificFeatureDataType dataType =
                Objects.requireNonNull(
                        entity.getDataType(),
                        "scientific feature data type is required"
                );

        return switch (dataType) {
            case NUMERIC ->
                    FeatureValue.numeric(
                            requireNumericValue(entity)
                    );

            case TEXT ->
                    FeatureValue.text(
                            requireTextValue(entity)
                    );

            case BOOLEAN ->
                    FeatureValue.bool(
                            requireBooleanValue(entity)
                    );
        };
    }

    private double requireNumericValue(
            ScientificFeatureItemEntity entity
    ) {
        if (
                entity.getNumericValue() == null
                        || entity.getTextValue() != null
                        || entity.getBooleanValue() != null
        ) {
            throw inconsistentEntity(
                    entity,
                    ScientificFeatureDataType.NUMERIC
            );
        }

        return entity.getNumericValue();
    }

    private String requireTextValue(
            ScientificFeatureItemEntity entity
    ) {
        if (
                entity.getTextValue() == null
                        || entity.getNumericValue() != null
                        || entity.getBooleanValue() != null
        ) {
            throw inconsistentEntity(
                    entity,
                    ScientificFeatureDataType.TEXT
            );
        }

        return entity.getTextValue();
    }

    private boolean requireBooleanValue(
            ScientificFeatureItemEntity entity
    ) {
        if (
                entity.getBooleanValue() == null
                        || entity.getNumericValue() != null
                        || entity.getTextValue() != null
        ) {
            throw inconsistentEntity(
                    entity,
                    ScientificFeatureDataType.BOOLEAN
            );
        }

        return entity.getBooleanValue();
    }

    private IllegalArgumentException inconsistentEntity(
            ScientificFeatureItemEntity entity,
            ScientificFeatureDataType expectedType
    ) {
        return new IllegalArgumentException(
                "Inconsistent scientific feature item entity "
                        + entity.getId()
                        + " for data type "
                        + expectedType
        );
    }
}