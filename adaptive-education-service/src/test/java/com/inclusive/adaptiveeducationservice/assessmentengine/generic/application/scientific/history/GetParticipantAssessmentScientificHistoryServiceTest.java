package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.AssessmentScientificHistoryQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GetParticipantAssessmentScientificHistoryServiceTest {

    private AssessmentScientificHistoryQueryPort historyQueryPort;
    private GetParticipantAssessmentScientificHistoryService service;

    @BeforeEach
    void setUp() {
        historyQueryPort =
                mock(
                        AssessmentScientificHistoryQueryPort.class
                );

        service =
                new GetParticipantAssessmentScientificHistoryService(
                        historyQueryPort
                );
    }

    @Test
    void shouldReturnHistoryUsingStableTemporalOrder() {
        Instant older =
                Instant.parse(
                        "2026-07-01T10:00:00Z"
                );

        Instant newer =
                Instant.parse(
                        "2026-07-10T10:00:00Z"
                );

        AssessmentScientificObservation newerB =
                observation(
                        "ADMIN-B",
                        newer
                );

        AssessmentScientificObservation olderObservation =
                observation(
                        "ADMIN-OLD",
                        older
                );

        AssessmentScientificObservation newerA =
                observation(
                        "ADMIN-A",
                        newer
                );

        when(
                historyQueryPort.findByParticipantId(
                        "ST-001"
                )
        ).thenReturn(
                List.of(
                        olderObservation,
                        newerB,
                        newerA
                )
        );

        var history =
                service.getByParticipantId(
                        " ST-001 "
                );

        assertThat(history.participantId())
                .isEqualTo("ST-001");

        assertThat(history.totalObservations())
                .isEqualTo(3);

        assertThat(history.firstSubmittedAt())
                .isEqualTo(older);

        assertThat(history.lastSubmittedAt())
                .isEqualTo(newer);

        assertThat(history.observations())
                .extracting(
                        AssessmentScientificObservation
                                ::administrationId
                )
                .containsExactly(
                        "ADMIN-A",
                        "ADMIN-B",
                        "ADMIN-OLD"
                );

        verify(historyQueryPort)
                .findByParticipantId(
                        "ST-001"
                );
    }

    @Test
    void shouldReturnEmptyHistory() {
        when(
                historyQueryPort.findByParticipantId(
                        "ST-EMPTY"
                )
        ).thenReturn(List.of());

        var history =
                service.getByParticipantId(
                        "ST-EMPTY"
                );

        assertThat(history.totalObservations())
                .isZero();

        assertThat(history.observations())
                .isEmpty();

        assertThat(history.firstSubmittedAt())
                .isNull();

        assertThat(history.lastSubmittedAt())
                .isNull();
    }

    @Test
    void shouldRejectBlankParticipantId() {
        assertThatThrownBy(() ->
                service.getByParticipantId(" ")
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "must not be blank"
                );

        verifyNoInteractions(historyQueryPort);
    }

    @Test
    void shouldRejectNullParticipantId() {
        assertThatThrownBy(() ->
                service.getByParticipantId(null)
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "participantId is required"
                );

        verifyNoInteractions(historyQueryPort);
    }

    private AssessmentScientificObservation observation(
            String administrationId,
            Instant submittedAt
    ) {
        return new AssessmentScientificObservation(
                administrationId,
                "ST-001",
                "KOLB_V1",
                "1.0",
                "DIVERGENT",
                "KOLB_BASELINE_V1",
                "KOLB_INTERPRETATION_V1",
                submittedAt.plusSeconds(1),
                submittedAt,
                submittedAt,
                List.of(),
                List.of(),
                null
        );
    }
}