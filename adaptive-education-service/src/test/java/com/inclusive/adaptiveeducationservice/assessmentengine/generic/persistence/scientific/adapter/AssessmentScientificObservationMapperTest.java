package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.adapter;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.PersistAssessmentScientificObservationCommand;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentScientificObservationMapperTest {

    private final AssessmentScientificObservationMapper mapper =
            new AssessmentScientificObservationMapper();

    @Test
    void shouldMapCompleteScientificObservation() {
        Instant submittedAt =
                Instant.parse("2026-07-22T10:00:00Z");

        Instant explicitCutoff =
                Instant.parse("2026-07-22T09:59:00Z");

        AssessmentSubmission submission =
                new AssessmentSubmission(
                        "ADMIN-001",
                        "ST-001",
                        "KOLB_V1",
                        "1.0",
                        List.of(),
                        Map.ofEntries(
                                Map.entry(
                                        "institutionId",
                                        "INST-001"
                                ),
                                Map.entry(
                                        "courseId",
                                        "10-1"
                                ),
                                Map.entry(
                                        "academicYear",
                                        "2026"
                                ),
                                Map.entry(
                                        "academicPeriod",
                                        "PILOT-1"
                                ),
                                Map.entry(
                                        "fieldworkPhase",
                                        "PILOT"
                                ),
                                Map.entry(
                                        "source",
                                        "WEB"
                                ),
                                Map.entry(
                                        "durationSeconds",
                                        "600"
                                ),
                                Map.entry(
                                        "featureCutoffAt",
                                        explicitCutoff.toString()
                                )
                        ),
                        submittedAt
                );

        AssessmentResult result =
                new AssessmentResult(
                        "ADMIN-001",
                        "ST-001",
                        "KOLB_V1",
                        "1.0",
                        "DIVERGENT",
                        Map.of(
                                "CE",
                                48.0,
                                "RO",
                                36.0,
                                "AC",
                                24.0,
                                "AE",
                                12.0,
                                "AC_MINUS_CE",
                                -24.0,
                                "AE_MINUS_RO",
                                -24.0
                        ),
                        Map.of(
                                "PRIMARY_PROFILE",
                                "Perfil divergente"
                        ),
                        List.of(
                                "Favorecer experiencias concretas"
                        ),
                        "KOLB_BASELINE_V1",
                        submittedAt.plusSeconds(1)
                );

        PersistAssessmentScientificObservationCommand command =
                new PersistAssessmentScientificObservationCommand(
                        "11111111-1111-1111-1111-111111111111",
                        submission,
                        result
                );

        var resultEntity =
                mapper.toResultEntity(command);
        assertThat(
                resultEntity.getParticipantId()
        ).isEqualTo(
                "11111111-1111-1111-1111-111111111111"
        );

        assertThat(
                resultEntity.getParticipantId()
        ).isNotEqualTo(
                "ST-001"
        );

        var contextEntity =
                mapper.toContextEntity(command);

        assertThat(resultEntity.getId())
                .isEqualTo(
                        "RESULT-ADMIN-001"
                );

        assertThat(resultEntity.getPrimaryProfile())
                .isEqualTo("DIVERGENT");

        assertThat(resultEntity.getScoringAlgorithmVersion())
                .isEqualTo(
                        "KOLB_BASELINE_V1"
                );

        assertThat(resultEntity.getScores())
                .hasSize(6);

        assertThat(
                resultEntity.getScores()
                        .stream()
                        .map(score ->
                                score.getDimensionCode()
                        )
        )
                .containsExactlyInAnyOrder(
                        "CE",
                        "RO",
                        "AC",
                        "AE",
                        "AC_MINUS_CE",
                        "AE_MINUS_RO"
                );

        assertThat(resultEntity.getInterpretations())
                .hasSize(1);

        assertThat(resultEntity.getFeatureCutoffAt())
                .isEqualTo(explicitCutoff);

        assertThat(contextEntity.getInstitutionId())
                .isEqualTo("INST-001");

        assertThat(contextEntity.getCourseId())
                .isEqualTo("10-1");

        assertThat(contextEntity.getDurationSeconds())
                .isEqualTo(600L);

        assertThat(contextEntity.getFeatureCutoffAt())
                .isEqualTo(explicitCutoff);

        assertThat(contextEntity.getContextJson())
                .containsEntry(
                        "fieldworkPhase",
                        "PILOT"
                );
    }

    @Test
    void shouldUseSubmittedAtAsDefaultFeatureCutoff() {
        Instant submittedAt =
                Instant.parse("2026-07-22T10:00:00Z");

        AssessmentSubmission submission =
                new AssessmentSubmission(
                        "ADMIN-002",
                        "ST-001",
                        "KOLB_V1",
                        "1.0",
                        List.of(),
                        Map.of(),
                        submittedAt
                );

        AssessmentResult result =
                new AssessmentResult(
                        "ADMIN-002",
                        "ST-001",
                        "KOLB_V1",
                        "1.0",
                        "DIVERGENT",
                        Map.of(),
                        Map.of(),
                        List.of(),
                        "KOLB_BASELINE_V1",
                        submittedAt.plusSeconds(1)
                );

        var command =
                new PersistAssessmentScientificObservationCommand(
                        "11111111-1111-1111-1111-111111111111",
                        submission,
                        result
                );

        assertThat(
                mapper.toResultEntity(command)
                        .getFeatureCutoffAt()
        ).isEqualTo(submittedAt);

        assertThat(
                mapper.toContextEntity(command)
                        .getFeatureCutoffAt()
        ).isEqualTo(submittedAt);
    }
}