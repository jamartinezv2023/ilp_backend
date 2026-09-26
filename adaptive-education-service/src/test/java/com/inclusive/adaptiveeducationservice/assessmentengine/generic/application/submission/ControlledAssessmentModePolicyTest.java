package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ControlledAssessmentModePolicyTest {

    private final ControlledAssessmentModePolicy policy =
            new ControlledAssessmentModePolicy();

    @Test
    void acceptsTraceableSyntheticDemo() {
        var request = request("DEMO", "DEMO-001");
        assertThat(policy.validateAndIsDemo(request)).isTrue();
    }

    @Test
    void blocksFieldworkForCandidateInstrument() {
        assertThatThrownBy(() ->
                policy.validateAndIsDemo(request("FIELDWORK", "ST-001"))
        ).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403");
    }

    @Test
    void blocksUnapprovedKolbEvenWithFieldworkContext() {
        var request = new SubmitAssessmentRequest(
                "ADMIN-UNAPPROVED", "ST-001", null,
                "KOLB_V1", "1.0", List.of(),
                Map.of("executionMode", "FIELDWORK"), null
        );
        assertThatThrownBy(() -> policy.validateAndIsDemo(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403")
                .hasMessageContaining("No instrument is approved");
    }

    @Test
    void blocksSyntheticInstrumentOutsideTestFixture() {
        var request = new SubmitAssessmentRequest(
                "SYNTHETIC-ADMIN", "SYNTHETIC-STUDENT-001", null,
                "ILP-SYNTHETIC-PHYSICS", "0.0.1-test", List.of(),
                Map.of("fieldworkPhase", "TEST_ONLY"), null
        );
        assertThatThrownBy(() -> policy.validateAndIsDemo(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403");
    }

    private SubmitAssessmentRequest request(String mode, String participantId) {
        return new SubmitAssessmentRequest(
                "DEMO-ADMIN",
                participantId,
                null,
                "ILP-MEA",
                "0.1.0-candidate",
                List.of(),
                Map.of(
                        "executionMode", mode,
                        "language", "es-CO",
                        "translationVersion", "0.1.0-original-es-CO"
                ),
                null
        );
    }
}
