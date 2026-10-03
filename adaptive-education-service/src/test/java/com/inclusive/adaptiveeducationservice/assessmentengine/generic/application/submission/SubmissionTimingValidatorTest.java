package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubmissionTimingValidatorTest {
    @Test
    void acceptsAbsentTimingAndConsistentElapsedSeconds() {
        assertThatCode(() -> SubmissionTimingValidator.validate(request(Map.of())))
                .doesNotThrowAnyException();
        assertThatCode(() -> SubmissionTimingValidator.validate(request(Map.of(
                "startedAt", "2026-10-02T10:00:00.500Z", "durationSeconds", "9"))))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsPartialMalformedNegativeAndInconsistentTiming() {
        for (var timing : List.of(
                Map.of("startedAt", "2026-10-02T10:00:00Z"),
                Map.of("durationSeconds", "10"),
                Map.of("startedAt", "invalid", "durationSeconds", "10"),
                Map.of("startedAt", "2026-10-02T10:00:00Z", "durationSeconds", "NaN"),
                Map.of("startedAt", "2026-10-02T10:00:00Z", "durationSeconds", "-1"),
                Map.of("startedAt", "2026-10-02T10:00:11Z", "durationSeconds", "0"),
                Map.of("startedAt", "2026-10-02T10:00:00Z", "durationSeconds", "11"))) {
            assertThatThrownBy(() -> SubmissionTimingValidator.validate(request(timing)))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("ASSESSMENT_TIMING_INVALID");
        }
    }

    private SubmitAssessmentRequest request(Map<String, String> context) {
        return new SubmitAssessmentRequest("SYNTHETIC-ADMIN-TIME", "SYNTHETIC-STUDENT-001",
                null, "KOLB_V1", "0.0.1-test", List.of(), context,
                Instant.parse("2026-10-02T10:00:10Z"));
    }
}
