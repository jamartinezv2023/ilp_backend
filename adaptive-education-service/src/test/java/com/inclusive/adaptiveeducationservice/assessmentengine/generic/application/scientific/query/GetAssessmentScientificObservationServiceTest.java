package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.AssessmentScientificObservationQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GetAssessmentScientificObservationServiceTest {

    private AssessmentScientificObservationQueryPort queryPort;
    private GetAssessmentScientificObservationService service;

    @BeforeEach
    void setUp() {
        queryPort =
                mock(
                        AssessmentScientificObservationQueryPort.class
                );

        service =
                new GetAssessmentScientificObservationService(
                        queryPort
                );
    }

    @Test
    void shouldReturnObservationUsingNormalizedAdministrationId() {
        AssessmentScientificObservation observation =
                observation("ADMIN-001");

        when(
                queryPort.findByAdministrationId(
                        "ADMIN-001"
                )
        ).thenReturn(
                Optional.of(observation)
        );

        AssessmentScientificObservation actual =
                service.getByAdministrationId(
                        " ADMIN-001 "
                );

        assertThat(actual)
                .isSameAs(observation);

        verify(queryPort)
                .findByAdministrationId(
                        "ADMIN-001"
                );
    }

    @Test
    void shouldThrowNotFoundWhenObservationDoesNotExist() {
        when(
                queryPort.findByAdministrationId(
                        "ADMIN-404"
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(() ->
                service.getByAdministrationId(
                        "ADMIN-404"
                )
        )
                .isInstanceOf(
                        AssessmentScientificObservationNotFoundException.class
                )
                .hasMessageContaining(
                        "ADMIN-404"
                );
    }

    @Test
    void shouldRejectBlankAdministrationId() {
        assertThatThrownBy(() ->
                service.getByAdministrationId(" ")
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "must not be blank"
                );

        verifyNoInteractions(queryPort);
    }

    @Test
    void shouldRejectNullAdministrationId() {
        assertThatThrownBy(() ->
                service.getByAdministrationId(null)
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "administrationId is required"
                );

        verifyNoInteractions(queryPort);
    }

    private AssessmentScientificObservation observation(
            String administrationId
    ) {
        Instant timestamp =
                Instant.parse(
                        "2026-07-24T10:00:00Z"
                );

        return new AssessmentScientificObservation(
                administrationId,
                "ST-001",
                "KOLB_V1",
                "1.0",
                "DIVERGENT",
                "KOLB_BASELINE_V1",
                "KOLB_INTERPRETATION_V1",
                timestamp,
                timestamp,
                timestamp,
                List.of(),
                List.of(),
                null
        );
    }
}