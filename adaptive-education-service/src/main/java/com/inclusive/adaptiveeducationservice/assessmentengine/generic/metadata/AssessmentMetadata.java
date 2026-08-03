package com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata;

import java.util.Objects;

public record AssessmentMetadata(

        String code,

        String name,

        String author,

        String version,

        AssessmentInstrumentType instrumentType,

        String language,

        Integer estimatedMinutes,

        String objective,

        String copyrightNotice
) {

    public AssessmentMetadata {
        requireText(code, "Assessment metadata code");
        requireText(name, "Assessment metadata name");
        requireText(version, "Assessment metadata version");

        instrumentType = Objects.requireNonNull(
                instrumentType,
                "Assessment instrument type is required"
        );

        language = defaultText(language, "es");
        author = defaultText(author, "ILP");
        objective = defaultText(
                objective,
                "Educational assessment"
        );

        copyrightNotice = defaultText(
                copyrightNotice,
                "Usage subject to institutional authorization"
        );

        if (
                estimatedMinutes == null
                || estimatedMinutes < 1
        ) {
            estimatedMinutes = 20;
        }
    }

    private static void requireText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required"
            );
        }
    }

    private static String defaultText(
            String value,
            String defaultValue
    ) {
        return value == null || value.isBlank()
                ? defaultValue
                : value;
    }
}