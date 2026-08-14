package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentQuestionRequest;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SubmitAssessmentMapperTest {

    private final SubmitAssessmentMapper mapper =
            new SubmitAssessmentMapper();

    @Test
    void shouldMapGenericIpsativeSubmissionToDomain() {
        var question =
                new SubmitAssessmentQuestionRequest(
                        "Q1",
                        List.of(),
                        Map.of(
                                "Q1-CE", 4,
                                "Q1-RO", 3,
                                "Q1-AC", 2,
                                "Q1-AE", 1
                        ),
                        null,
                        null
                );

        Instant submittedAt =
                Instant.parse("2026-07-21T10:00:00Z");

        var request =
                new SubmitAssessmentRequest(
                        "ADMIN-001",
                        "ST-001",
                        UUID.fromString(
                                "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
                        ),
                        "KOLB_V1",
                        "1.0",
                        List.of(question),
                        Map.of(
                                "source", "WEB",
                                "fieldworkPhase", "PILOT"
                        ),
                        submittedAt
                );

        var submission = mapper.toDomain(request);

        assertThat(submission.administrationId())
                .isEqualTo("ADMIN-001");

        assertThat(submission.participantId())
                .isEqualTo("ST-001");

        assertThat(submission.assessmentCode())
                .isEqualTo("KOLB_V1");

        assertThat(submission.assessmentVersion())
                .isEqualTo("1.0");

        assertThat(submission.responses())
                .hasSize(1);

        assertThat(submission.responses().get(0).rankings())
                .containsEntry("Q1-CE", 4)
                .containsEntry("Q1-RO", 3)
                .containsEntry("Q1-AC", 2)
                .containsEntry("Q1-AE", 1);

        assertThat(submission.context())
                .containsEntry("source", "WEB");

        assertThat(submission.submittedAt())
                .isEqualTo(submittedAt);
    }

    @Test
    void shouldNormalizeNullCollections() {
        var question =
                new SubmitAssessmentQuestionRequest(
                        "Q1",
                        null,
                        null,
                        null,
                        null
                );

        assertThat(question.selectedOptionIds())
                .isEmpty();

        assertThat(question.rankings())
                .isEmpty();
    }
}