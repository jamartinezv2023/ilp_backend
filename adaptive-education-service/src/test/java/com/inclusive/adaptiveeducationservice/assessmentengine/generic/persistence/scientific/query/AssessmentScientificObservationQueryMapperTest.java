package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.query;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentInterpretationEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentScientificResultEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentScoreItemEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentSubmissionContextEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentScientificObservationQueryMapperTest {

    private final AssessmentScientificObservationQueryMapper mapper =
            new AssessmentScientificObservationQueryMapper();

    @Test
    void shouldReconstructCompleteScientificObservation() {
        Instant submittedAt =
                Instant.parse(
                        "2026-07-24T10:00:00Z"
                );

        Instant calculatedAt =
                submittedAt.plusSeconds(1);

        AssessmentScientificResultEntity result =
                new AssessmentScientificResultEntity(
                        "RESULT-ADMIN-001",
                        "ADMIN-001",
                        "ST-001",
                        "KOLB_V1",
                        "1.0",
                        "DIVERGENT",
                        "KOLB_BASELINE_V1",
                        "KOLB_INTERPRETATION_V1",
                        calculatedAt,
                        submittedAt,
                        submittedAt
                );

        result.addScore(
                new AssessmentScoreItemEntity(
                        "SCORE-CE-ADMIN-001",
                        "ADMIN-001",
                        "CE",
                        48.0
                )
        );

        result.addScore(
                new AssessmentScoreItemEntity(
                        "SCORE-RO-ADMIN-001",
                        "ADMIN-001",
                        "RO",
                        36.0
                )
        );

        result.addInterpretation(
                new AssessmentInterpretationEntity(
                        "INTERPRETATION-ADMIN-001",
                        "ADMIN-001",
                        "PRIMARY_PROFILE",
                        "Perfil divergente",
                        "KOLB_INTERPRETATION_V1"
                )
        );

        AssessmentSubmissionContextEntity context =
                new AssessmentSubmissionContextEntity(
                        "CONTEXT-ADMIN-001",
                        "ADMIN-001",
                        submittedAt
                );

        context.defineAcademicContext(
                "INST-001",
                "CAMPUS-001",
                "PROGRAM-001",
                "COURSE-001",
                "COHORT-001",
                "TEACHER-001",
                "10",
                "2026",
                "1"
        );

        context.defineFieldworkContext(
                "PILOT",
                "INTERVENTION-001",
                "CONTROL",
                "CONSENT-001",
                "1.0",
                "ETHICS-001"
        );

        context.defineTechnicalContext(
                "WEB",
                "ONLINE",
                "es",
                "DESKTOP",
                "Chrome",
                "Windows",
                "America/Bogota",
                "MVP-21A"
        );

        context.defineFeatureContext(
                "FEATURES-V1",
                "PREPROCESSING-V1",
                "NORMALIZATION-V1"
        );

        context.defineTiming(
                submittedAt.minusSeconds(600),
                600L
        );

        context.replaceContextJson(
                Map.of(
                        "fieldworkPhase",
                        "PILOT",
                        "source",
                        "WEB"
                )
        );

        var observation =
                mapper.toModel(
                        result,
                        context
                );

        assertThat(observation.administrationId())
                .isEqualTo("ADMIN-001");

        assertThat(observation.participantId())
                .isEqualTo("ST-001");

        assertThat(observation.primaryProfile())
                .isEqualTo("DIVERGENT");

        assertThat(observation.scores())
                .hasSize(2);

        assertThat(observation.interpretations())
                .hasSize(1);

        assertThat(observation.context())
                .isNotNull();

        assertThat(observation.context().institutionId())
                .isEqualTo("INST-001");

        assertThat(observation.context().durationSeconds())
                .isEqualTo(600L);

        assertThat(observation.context().completeContext())
                .containsEntry(
                        "source",
                        "WEB"
                );

        assertThat(observation.featureCutoffAt())
                .isEqualTo(submittedAt);
    }
}