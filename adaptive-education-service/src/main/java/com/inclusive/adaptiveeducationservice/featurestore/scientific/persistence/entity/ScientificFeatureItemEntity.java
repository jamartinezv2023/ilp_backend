package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Objects;

@Entity
@Table(
        name = "scientific_feature_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_scientific_feature_item_code",
                        columnNames = {
                                "feature_vector_id",
                                "feature_code"
                        }
                )
        }
)
public class ScientificFeatureItemEntity {

    @Id
    @Column(
            name = "id",
            nullable = false,
            length = 120
    )
    private String id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "feature_vector_id",
            nullable = false
    )
    private ScientificFeatureVectorEntity featureVector;

    @Column(
            name = "feature_code",
            nullable = false,
            length = 100
    )
    private String featureCode;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "data_type",
            nullable = false,
            length = 30
    )
    private ScientificFeatureDataType dataType;

    @Column(name = "numeric_value")
    private Double numericValue;

    @Column(name = "text_value")
    private String textValue;

    @Column(name = "boolean_value")
    private Boolean booleanValue;

    @Column(
            name = "source_assessment_code",
            length = 100
    )
    private String sourceAssessmentCode;

    @Column(
            name = "source_administration_id",
            length = 100
    )
    private String sourceAdministrationId;

    protected ScientificFeatureItemEntity() {
    }

    private ScientificFeatureItemEntity(
            String id,
            String featureCode,
            ScientificFeatureDataType dataType,
            Double numericValue,
            String textValue,
            Boolean booleanValue,
            String sourceAssessmentCode,
            String sourceAdministrationId
    ) {
        this.id = requireText(id, "id");
        this.featureCode =
                requireText(
                        featureCode,
                        "featureCode"
                );
        this.dataType =
                Objects.requireNonNull(
                        dataType,
                        "dataType is required"
                );
        this.numericValue = numericValue;
        this.textValue = textValue;
        this.booleanValue = booleanValue;
        this.sourceAssessmentCode =
                normalizeOptional(
                        sourceAssessmentCode
                );
        this.sourceAdministrationId =
                normalizeOptional(
                        sourceAdministrationId
                );

        validateTypedValue();
    }

    public static ScientificFeatureItemEntity numeric(
            String id,
            String featureCode,
            Double value,
            String sourceAssessmentCode,
            String sourceAdministrationId
    ) {
        return new ScientificFeatureItemEntity(
                id,
                featureCode,
                ScientificFeatureDataType.NUMERIC,
                Objects.requireNonNull(
                        value,
                        "numeric value is required"
                ),
                null,
                null,
                sourceAssessmentCode,
                sourceAdministrationId
        );
    }

    public static ScientificFeatureItemEntity text(
            String id,
            String featureCode,
            String value,
            String sourceAssessmentCode,
            String sourceAdministrationId
    ) {
        return new ScientificFeatureItemEntity(
                id,
                featureCode,
                ScientificFeatureDataType.TEXT,
                null,
                requireText(value, "text value"),
                null,
                sourceAssessmentCode,
                sourceAdministrationId
        );
    }

    public static ScientificFeatureItemEntity bool(
            String id,
            String featureCode,
            Boolean value,
            String sourceAssessmentCode,
            String sourceAdministrationId
    ) {
        return new ScientificFeatureItemEntity(
                id,
                featureCode,
                ScientificFeatureDataType.BOOLEAN,
                null,
                null,
                Objects.requireNonNull(
                        value,
                        "boolean value is required"
                ),
                sourceAssessmentCode,
                sourceAdministrationId
        );
    }

    void assignFeatureVector(
            ScientificFeatureVectorEntity featureVector
    ) {
        this.featureVector = featureVector;
    }

    private void validateTypedValue() {
        int populatedValues = 0;

        if (numericValue != null) {
            populatedValues++;
        }

        if (textValue != null) {
            populatedValues++;
        }

        if (booleanValue != null) {
            populatedValues++;
        }

        if (populatedValues != 1) {
            throw new IllegalArgumentException(
                    "Exactly one feature value is required"
            );
        }

        if (
                dataType == ScientificFeatureDataType.NUMERIC
                        && numericValue == null
        ) {
            throw new IllegalArgumentException(
                    "NUMERIC feature requires numericValue"
            );
        }

        if (
                dataType == ScientificFeatureDataType.TEXT
                        && textValue == null
        ) {
            throw new IllegalArgumentException(
                    "TEXT feature requires textValue"
            );
        }

        if (
                dataType == ScientificFeatureDataType.BOOLEAN
                        && booleanValue == null
        ) {
            throw new IllegalArgumentException(
                    "BOOLEAN feature requires booleanValue"
            );
        }
    }

    public String getId() {
        return id;
    }

    public ScientificFeatureVectorEntity getFeatureVector() {
        return featureVector;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public ScientificFeatureDataType getDataType() {
        return dataType;
    }

    public Double getNumericValue() {
        return numericValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public String getSourceAssessmentCode() {
        return sourceAssessmentCode;
    }

    public String getSourceAdministrationId() {
        return sourceAdministrationId;
    }

    private static String requireText(
            String value,
            String field
    ) {
        Objects.requireNonNull(
                value,
                field + " is required"
        );

        String normalized = value.trim();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    field + " must not be blank"
            );
        }

        return normalized;
    }

    private static String normalizeOptional(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }
}