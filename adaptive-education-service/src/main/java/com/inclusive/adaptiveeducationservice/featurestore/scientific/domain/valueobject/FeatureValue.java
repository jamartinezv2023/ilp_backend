package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject;

import java.util.Objects;

public sealed interface FeatureValue
        permits FeatureValue.NumericValue,
        FeatureValue.TextValue,
        FeatureValue.BooleanValue {

    DataType dataType();

    enum DataType {
        NUMERIC,
        TEXT,
        BOOLEAN
    }

    record NumericValue(double value)
            implements FeatureValue {

        public NumericValue {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException(
                        "numeric feature value must be finite"
                );
            }
        }

        @Override
        public DataType dataType() {
            return DataType.NUMERIC;
        }
    }

    record TextValue(String value)
            implements FeatureValue {

        public TextValue {
            Objects.requireNonNull(
                    value,
                    "text feature value is required"
            );

            value = value.trim();

            if (value.isEmpty()) {
                throw new IllegalArgumentException(
                        "text feature value must not be blank"
                );
            }
        }

        @Override
        public DataType dataType() {
            return DataType.TEXT;
        }
    }

    record BooleanValue(boolean value)
            implements FeatureValue {

        @Override
        public DataType dataType() {
            return DataType.BOOLEAN;
        }
    }

    static FeatureValue numeric(double value) {
        return new NumericValue(value);
    }

    static FeatureValue text(String value) {
        return new TextValue(value);
    }

    static FeatureValue bool(boolean value) {
        return new BooleanValue(value);
    }
}