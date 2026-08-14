package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersistAssessmentScientificObservationCommandTest {

    private static final Instant SUBMITTED_AT =
            Instant.parse("2026-07-22T10:00:00Z");

    @Test
    void shouldCreateCommandForMatchingObservation() {
        AssessmentSubmission submission =
                submission(
                        "ADMIN-001",
                        "ST-001",
                        "KOLB_V1"
                );

        AssessmentResult result =
                result(
                        "ADMIN-001",
                        "ST-001",
                        "KOLB_V1"
                );

        PersistAssessmentScientificObservationCommand command =
                new PersistAssessmentScientificObservationCommand(
                        "11111111-1111-1111-1111-111111111111",
                        submission,
                        result
                );

        assertThat(command.submission())
                .isSameAs(submission);

        assertThat(command.result())
                .isSameAs(result);
    }

    @Test
    void shouldRejectDifferentAdministration() {
        assertThatThrownBy(() ->
                new PersistAssessmentScientificObservationCommand(
                        "11111111-1111-1111-1111-111111111111",
                        submission(
                                "ADMIN-001",
                                "ST-001",
                                "KOLB_V1"
                        ),
                        result(
                                "ADMIN-002",
                                "ST-001",
                                "KOLB_V1"
                        )
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "administration ids must match"
                );
    }

    @Test
    void shouldRejectDifferentParticipant() {
        assertThatThrownBy(() ->
                new PersistAssessmentScientificObservationCommand(
                        "11111111-1111-1111-1111-111111111111",
                        submission(
                                "ADMIN-001",
                                "ST-001",
                                "KOLB_V1"
                        ),
                        result(
                                "ADMIN-001",
                                "ST-002",
                                "KOLB_V1"
                        )
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "participant ids must match"
                );
    }

    @Test
    void shouldRejectDifferentAssessmentCode() {
        assertThatThrownBy(() ->
                new PersistAssessmentScientificObservationCommand(
                        "11111111-1111-1111-1111-111111111111",
                        submission(
                                "ADMIN-001",
                                "ST-001",
                                "KOLB_V1"
                        ),
                        result(
                                "ADMIN-001",
                                "ST-001",
                                "KUDER_V1"
                        )
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "assessment codes must match"
                );
    }

    @Test
    void shouldRejectNullSubmission() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new PersistAssessmentScientificObservationCommand(
                                "11111111-1111-1111-1111-111111111111",
                                null,
                                result(
                                        "ADMIN-001",
                                        "ST-001",
                                        "KOLB_V1"
                                )
                        )
                )
                .withMessage(
                        "submission is required"
                );
    }

    private AssessmentSubmission submission(
            String administrationId,
            String participantId,
            String assessmentCode
    ) {
        return new AssessmentSubmission(
                administrationId,
                participantId,
                assessmentCode,
                "1.0",
                List.of(),
                Map.of(),
                SUBMITTED_AT
        );
    }

    private AssessmentResult result(
            String administrationId,
            String participantId,
            String assessmentCode
    ) {
        return new AssessmentResult(
                administrationId,
                participantId,
                assessmentCode,
                "1.0",
                "DIVERGENT",
                Map.of(
                        "CE",
                        48.0
                ),
                Map.of(
                        "PRIMARY_PROFILE",
                        "Perfil divergente"
                ),
                List.of(),
                "KOLB_BASELINE_V1",
                SUBMITTED_AT.plusSeconds(1)
        );
    }

    @Test
    void shouldRejectNullResearchSubjectId() {

        assertThatThrownBy(
                () ->
                        new PersistAssessmentScientificObservationCommand(
                                null,
                                submission(
                                        "ADMIN-001",
                                        "ST-001",
                                        "KOLB_V1"
                                ),
                                result(
                                        "ADMIN-001",
                                        "ST-001",
                                        "KOLB_V1"
                                )
                        )
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "researchSubjectId is required"
                );
    }

    @Test
    void shouldRejectBlankResearchSubjectId() {

        assertThatThrownBy(
                () ->
                        new PersistAssessmentScientificObservationCommand(
                                "   ",
                                submission(
                                        "ADMIN-001",
                                        "ST-001",
                                        "KOLB_V1"
                                ),
                                result(
                                        "ADMIN-001",
                                        "ST-001",
                                        "KOLB_V1"
                                )
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "researchSubjectId must not be blank"
                );
    }
}