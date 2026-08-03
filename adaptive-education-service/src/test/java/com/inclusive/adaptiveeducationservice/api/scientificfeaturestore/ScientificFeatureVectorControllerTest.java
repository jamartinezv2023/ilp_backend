package com.inclusive.adaptiveeducationservice.api.scientificfeaturestore;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.ScientificFeatureVectorQueryUseCase;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindExactScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindLatestScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result.ScientificFeatureItemResult;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result.ScientificFeatureVectorResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ScientificFeatureVectorControllerTest {

    private static final String PARTICIPANT_ID =
            "PARTICIPANT-REST-001";

    private static final String FEATURE_SET_VERSION =
            "SCIENTIFIC-FEATURES-V1";

    private static final Instant FEATURE_CUTOFF_AT =
            Instant.parse(
                    "2026-08-02T12:00:00Z"
            );

    private ScientificFeatureVectorQueryUseCase queryUseCase;

    private ScientificFeatureVectorController controller;

    @BeforeEach
    void setUp() {
        queryUseCase =
                mock(
                        ScientificFeatureVectorQueryUseCase.class
                );

        controller =
                new ScientificFeatureVectorController(
                        queryUseCase
                );
    }

    @Test
    void shouldRejectNullQueryUseCase() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new ScientificFeatureVectorController(
                                null
                        )
                )
                .withMessageContaining(
                        "queryUseCase"
                );
    }

    @Test
    void shouldReturnExactVectorWithHttp200() {
        when(
                queryUseCase.findExact(
                        new FindExactScientificFeatureVectorQuery(
                                new ParticipantId(PARTICIPANT_ID),
                                new FeatureSetVersion(
                                        FEATURE_SET_VERSION
                                ),
                                FEATURE_CUTOFF_AT
                        )
                )
        ).thenReturn(
                Optional.of(
                        result("VECTOR-EXACT-REST-001")
                )
        );

        ResponseEntity<ScientificFeatureVectorResult> response =
                controller.findExact(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION,
                        FEATURE_CUTOFF_AT
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
                .isNotNull()
                .extracting(
                        ScientificFeatureVectorResult::vectorId
                )
                .isEqualTo(
                        "VECTOR-EXACT-REST-001"
                );
    }

    @Test
    void shouldThrowNotFoundWhenExactVectorDoesNotExist() {
        when(
                queryUseCase.findExact(
                        new FindExactScientificFeatureVectorQuery(
                                new ParticipantId(PARTICIPANT_ID),
                                new FeatureSetVersion(
                                        FEATURE_SET_VERSION
                                ),
                                FEATURE_CUTOFF_AT
                        )
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(() ->
                controller.findExact(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION,
                        FEATURE_CUTOFF_AT
                )
        )
                .isInstanceOf(
                        ResponseStatusException.class
                )
                .hasMessageContaining(
                        "404 NOT_FOUND"
                )
                .hasMessageContaining(
                        "Exact scientific feature vector not found"
                );
    }
    @Test
    void shouldBuildExactQueryFromHttpParameters() {
        when(
                queryUseCase.findExact(
                        org.mockito.ArgumentMatchers.any()
                )
        ).thenReturn(
                Optional.of(result("VECTOR-EXACT-CAPTOR-001"))
        );

        controller.findExact(
                PARTICIPANT_ID,
                FEATURE_SET_VERSION,
                FEATURE_CUTOFF_AT
        );

        ArgumentCaptor<FindExactScientificFeatureVectorQuery>
                queryCaptor =
                ArgumentCaptor.forClass(
                        FindExactScientificFeatureVectorQuery.class
                );

        verify(queryUseCase)
                .findExact(
                        queryCaptor.capture()
                );

        FindExactScientificFeatureVectorQuery query =
                queryCaptor.getValue();

        assertThat(query.participantId())
                .isEqualTo(
                        new ParticipantId(PARTICIPANT_ID)
                );

        assertThat(query.featureSetVersion())
                .isEqualTo(
                        new FeatureSetVersion(
                                FEATURE_SET_VERSION
                        )
                );

        assertThat(query.featureCutoffAt())
                .isEqualTo(FEATURE_CUTOFF_AT);

        verifyNoMoreInteractions(queryUseCase);
    }

    @Test
    void shouldReturnLatestCompletedVectorWithHttp200() {
        when(
                queryUseCase.findLatestCompleted(
                        new FindLatestScientificFeatureVectorQuery(
                                new ParticipantId(PARTICIPANT_ID),
                                new FeatureSetVersion(
                                        FEATURE_SET_VERSION
                                )
                        )
                )
        ).thenReturn(
                Optional.of(
                        result("VECTOR-LATEST-REST-001")
                )
        );

        ResponseEntity<ScientificFeatureVectorResult> response =
                controller.findLatestCompleted(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
                .isNotNull()
                .extracting(
                        ScientificFeatureVectorResult::vectorId
                )
                .isEqualTo(
                        "VECTOR-LATEST-REST-001"
                );
    }

    @Test
    void shouldThrowNotFoundWhenLatestVectorDoesNotExist() {
        when(
                queryUseCase.findLatestCompleted(
                        new FindLatestScientificFeatureVectorQuery(
                                new ParticipantId(PARTICIPANT_ID),
                                new FeatureSetVersion(
                                        FEATURE_SET_VERSION
                                )
                        )
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(() ->
                controller.findLatestCompleted(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION
                )
        )
                .isInstanceOf(
                        ResponseStatusException.class
                )
                .hasMessageContaining(
                        "404 NOT_FOUND"
                )
                .hasMessageContaining(
                        "Latest completed scientific feature vector not found"
                );
    }
    @Test
    void shouldBuildLatestQueryFromHttpParameters() {
        when(
                queryUseCase.findLatestCompleted(
                        org.mockito.ArgumentMatchers.any()
                )
        ).thenReturn(
                Optional.of(result("VECTOR-LATEST-CAPTOR-001"))
        );

        controller.findLatestCompleted(
                PARTICIPANT_ID,
                FEATURE_SET_VERSION
        );

        ArgumentCaptor<FindLatestScientificFeatureVectorQuery>
                queryCaptor =
                ArgumentCaptor.forClass(
                        FindLatestScientificFeatureVectorQuery.class
                );

        verify(queryUseCase)
                .findLatestCompleted(
                        queryCaptor.capture()
                );

        FindLatestScientificFeatureVectorQuery query =
                queryCaptor.getValue();

        assertThat(query.participantId())
                .isEqualTo(
                        new ParticipantId(PARTICIPANT_ID)
                );

        assertThat(query.featureSetVersion())
                .isEqualTo(
                        new FeatureSetVersion(
                                FEATURE_SET_VERSION
                        )
                );

        verifyNoMoreInteractions(queryUseCase);
    }

    private ScientificFeatureVectorResult result(
            String vectorId
    ) {
        return new ScientificFeatureVectorResult(
                vectorId,
                PARTICIPANT_ID,
                FEATURE_SET_VERSION,
                "GENERATOR-V1",
                FEATURE_CUTOFF_AT,
                FEATURE_CUTOFF_AT.plusSeconds(5),
                1,
                "CHECKSUM-REST-001",
                List.of(
                        ScientificFeatureItemResult.numeric(
                                "ITEM-REST-001",
                                "KOLB_CE",
                                25.0,
                                "KOLB",
                                "ADMIN-REST-001"
                        )
                )
        );
    }
}
