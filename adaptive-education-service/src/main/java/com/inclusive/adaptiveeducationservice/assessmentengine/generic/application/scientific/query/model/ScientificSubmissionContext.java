package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model;

import java.time.Instant;
import java.util.Map;

public record ScientificSubmissionContext(
        String institutionId,
        String campusId,
        String programId,
        String courseId,
        String cohortId,
        String teacherId,
        String grade,
        String academicYear,
        String academicPeriod,
        String fieldworkPhase,
        String interventionId,
        String interventionGroup,
        String consentId,
        String consentVersion,
        String ethicsProtocol,
        String source,
        String deliveryMode,
        String language,
        String deviceType,
        String browser,
        String operatingSystem,
        String timezone,
        String applicationVersion,
        String featureSetVersion,
        String preprocessingVersion,
        String normalizationVersion,
        Instant startedAt,
        Long durationSeconds,
        Instant featureCutoffAt,
        Map<String, Object> completeContext
) {

    public ScientificSubmissionContext {
        completeContext = completeContext == null
                ? Map.of()
                : Map.copyOf(completeContext);
    }
}