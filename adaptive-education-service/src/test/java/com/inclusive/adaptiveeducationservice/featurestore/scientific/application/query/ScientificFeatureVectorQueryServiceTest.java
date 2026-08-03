package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.query;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.query.mapper.ScientificFeatureVectorResultMapper;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificChecksum;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindExactScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindLatestScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result.ScientificFeatureItemResult;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result.ScientificFeatureVectorResult;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureVectorQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ScientificFeatureVectorQueryServiceTest {

    private static final ParticipantId PARTICIPANT_ID =
            new ParticipantId(
                    "PARTICIPANT-QUERY-001"
            );

    private static final FeatureSetVersion FEATURE_SET_VERSION =
            new FeatureSetVersion(
                    "SCIENTIFIC-FEATURES-V1"
            );

    private static final Instant FEATURE_CUTOFF_AT =
            Instant.parse(
                    "2026-08-02T12:00:00Z"
            );

    private ScientificFeatureVectorQueryPort queryPort;

    private ScientificFeatureVectorResultMapper resultMapper;

    private ScientificFeatureVectorQueryService service;

    @BeforeEach
    void setUp() {
        queryPort =
                mock(
                        ScientificFeatureVectorQueryPort.class
                );

        resultMapper =
                new ScientificFeatureVectorResultMapper();

        service =
                new ScientificFeatureVectorQueryService(
                        queryPort,
                        resultMapper
                );
    }

    @Test
    void shouldRejectNullQueryPort() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new ScientificFeatureVectorQueryService(
                                null,
                                resultMapper
                        )
                )
                .withMessageContaining(
                        "queryPort"
                );
    }

    @Test
    void shouldRejectNullResultMapper() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new ScientificFeatureVectorQueryService(
                                queryPort,
                                null
                        )
                )
                .withMessageContaining(
                        "resultMapper"
                );
    }

    @Test
    void shouldFindExactVectorAsResultModel() {
        FindExactScientificFeatureVectorQuery query =
                new FindExactScientificFeatureVectorQuery(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION,
                        FEATURE_CUTOFF_AT
                );

        ScientificFeatureVector domainVector =
                vector(
                        "VECTOR-EXACT-001"
                );

        when(
                queryPort.findExact(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION,
                        FEATURE_CUTOFF_AT
                )
        ).thenReturn(
                Optional.of(domainVector)
        );

        Optional<ScientificFeatureVectorResult> result =
                service.findExact(query);

        assertThat(result)
                .isPresent();

        ScientificFeatureVectorResult queryResult =
                result.orElseThrow();

        assertThat(queryResult.vectorId())
                .isEqualTo(
                        "VECTOR-EXACT-001"
                );

        assertThat(queryResult.participantId())
                .isEqualTo(
                        PARTICIPANT_ID.value()
                );

        assertThat(queryResult.featureSetVersion())
                .isEqualTo(
                        FEATURE_SET_VERSION.value()
                );

        assertThat(queryResult.features())
                .hasSize(1);

        assertThat(queryResult.features().get(0).dataType())
                .isEqualTo(
                        ScientificFeatureItemResult.DataType.NUMERIC
                );

        assertThat(queryResult.features().get(0).numericValue())
                .isEqualTo(25.0);

        verify(queryPort)
                .findExact(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION,
                        FEATURE_CUTOFF_AT
                );

        verifyNoMoreInteractions(queryPort);
    }

    @Test
    void shouldReturnEmptyWhenExactVectorDoesNotExist() {
        FindExactScientificFeatureVectorQuery query =
                new FindExactScientificFeatureVectorQuery(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION,
                        FEATURE_CUTOFF_AT
                );

        when(
                queryPort.findExact(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION,
                        FEATURE_CUTOFF_AT
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThat(
                service.findExact(query)
        ).isEmpty();

        verify(queryPort)
                .findExact(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION,
                        FEATURE_CUTOFF_AT
                );

        verifyNoMoreInteractions(queryPort);
    }

    @Test
    void shouldFindLatestCompletedVectorAsResultModel() {
        FindLatestScientificFeatureVectorQuery query =
                new FindLatestScientificFeatureVectorQuery(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION
                );

        ScientificFeatureVector domainVector =
                vector(
                        "VECTOR-LATEST-001"
                );

        when(
                queryPort.findLatestCompleted(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION
                )
        ).thenReturn(
                Optional.of(domainVector)
        );

        Optional<ScientificFeatureVectorResult> result =
                service.findLatestCompleted(query);

        assertThat(result)
                .isPresent();

        ScientificFeatureVectorResult queryResult =
                result.orElseThrow();

        assertThat(queryResult.vectorId())
                .isEqualTo(
                        "VECTOR-LATEST-001"
                );

        assertThat(queryResult.checksum())
                .isEqualTo(
                        "CHECKSUM-QUERY-001"
                );

        verify(queryPort)
                .findLatestCompleted(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION
                );

        verifyNoMoreInteractions(queryPort);
    }

    @Test
    void shouldReturnEmptyWhenLatestCompletedDoesNotExist() {
        FindLatestScientificFeatureVectorQuery query =
                new FindLatestScientificFeatureVectorQuery(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION
                );

        when(
                queryPort.findLatestCompleted(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThat(
                service.findLatestCompleted(query)
        ).isEmpty();

        verify(queryPort)
                .findLatestCompleted(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION
                );

        verifyNoMoreInteractions(queryPort);
    }

    @Test
    void shouldRejectNullExactQueryWithoutCallingPort() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        service.findExact(null)
                )
                .withMessageContaining(
                        "query"
                );

        verifyNoInteractions(queryPort);
    }

    @Test
    void shouldRejectNullLatestQueryWithoutCallingPort() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        service.findLatestCompleted(null)
                )
                .withMessageContaining(
                        "query"
                );

        verifyNoInteractions(queryPort);
    }

    @Test
    void shouldRejectNullExactResultFromPort() {
        FindExactScientificFeatureVectorQuery query =
                new FindExactScientificFeatureVectorQuery(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION,
                        FEATURE_CUTOFF_AT
                );

        when(
                queryPort.findExact(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION,
                        FEATURE_CUTOFF_AT
                )
        ).thenReturn(null);

        assertThatNullPointerException()
                .isThrownBy(() ->
                        service.findExact(query)
                )
                .withMessageContaining(
                        "queryPort result"
                );
    }

    @Test
    void shouldRejectNullLatestResultFromPort() {
        FindLatestScientificFeatureVectorQuery query =
                new FindLatestScientificFeatureVectorQuery(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION
                );

        when(
                queryPort.findLatestCompleted(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION
                )
        ).thenReturn(null);

        assertThatNullPointerException()
                .isThrownBy(() ->
                        service.findLatestCompleted(query)
                )
                .withMessageContaining(
                        "queryPort result"
                );
    }

    private ScientificFeatureVector vector(
            String vectorId
    ) {
        return new ScientificFeatureVector(
                new ScientificFeatureVectorId(
                        vectorId
                ),
                PARTICIPANT_ID,
                FEATURE_SET_VERSION,
                new GeneratorVersion(
                        "GENERATOR-V1"
                ),
                FEATURE_CUTOFF_AT,
                FEATURE_CUTOFF_AT.plusSeconds(5),
                1,
                new ScientificChecksum(
                        "CHECKSUM-QUERY-001"
                ),
                List.of(
                        new ScientificFeatureItem(
                                "FEATURE-QUERY-001",
                                new FeatureCode(
                                        "KOLB_CE"
                                ),
                                FeatureValue.numeric(
                                        25.0
                                ),
                                "KOLB",
                                "ADMIN-QUERY-001"
                        )
                )
        );
    }
}
