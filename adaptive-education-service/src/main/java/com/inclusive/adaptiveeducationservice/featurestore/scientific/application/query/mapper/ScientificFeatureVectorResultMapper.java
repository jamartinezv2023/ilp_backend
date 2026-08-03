package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.query.mapper;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result.ScientificFeatureItemResult;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result.ScientificFeatureVectorResult;

import java.util.List;
import java.util.Objects;

public final class ScientificFeatureVectorResultMapper {

    public ScientificFeatureVectorResult toResult(
            ScientificFeatureVector vector
    ) {
        Objects.requireNonNull(
                vector,
                "vector is required"
        );

        List<ScientificFeatureItemResult> features =
                vector.items()
                        .stream()
                        .map(this::toItemResult)
                        .toList();

        return new ScientificFeatureVectorResult(
                vector.id().value(),
                vector.participantId().value(),
                vector.featureSetVersion().value(),
                vector.generatorVersion().value(),
                vector.featureCutoffAt(),
                vector.generatedAt(),
                vector.sourceObservationCount(),
                vector.checksum().value(),
                features
        );
    }

    public ScientificFeatureItemResult toItemResult(
            ScientificFeatureItem item
    ) {
        Objects.requireNonNull(
                item,
                "item is required"
        );

        FeatureValue value =
                Objects.requireNonNull(
                        item.value(),
                        "item value is required"
                );

        if (value instanceof FeatureValue.NumericValue numericValue) {
            return ScientificFeatureItemResult.numeric(
                    item.id(),
                    item.featureCode().value(),
                    numericValue.value(),
                    item.sourceAssessmentCode(),
                    item.sourceAdministrationId()
            );
        }

        if (value instanceof FeatureValue.TextValue textValue) {
            return ScientificFeatureItemResult.text(
                    item.id(),
                    item.featureCode().value(),
                    textValue.value(),
                    item.sourceAssessmentCode(),
                    item.sourceAdministrationId()
            );
        }

        if (value instanceof FeatureValue.BooleanValue booleanValue) {
            return ScientificFeatureItemResult.bool(
                    item.id(),
                    item.featureCode().value(),
                    booleanValue.value(),
                    item.sourceAssessmentCode(),
                    item.sourceAdministrationId()
            );
        }

        throw new IllegalArgumentException(
                "Unsupported scientific feature value type: "
                        + value.getClass().getName()
        );
    }
}
