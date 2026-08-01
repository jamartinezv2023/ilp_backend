package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.idempotency;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificChecksum;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;
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

class ScientificGenerationIdempotencyServiceTest {

    private static final Instant CUTOFF =
            Instant.parse(
                    "2026-07-31T21:00:00Z"
            );

    private ScientificFeatureVectorQueryPort queryPort;
    private ScientificGenerationIdempotencyService service;

    @BeforeEach
    void setUp() {
        queryPort =
                mock(
                        ScientificFeatureVectorQueryPort.class
                );

        service =
                new ScientificGenerationIdempotencyService(
                        queryPort
                );
    }

    @Test
    void shouldRejectNullQueryPort() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new ScientificGenerationIdempotencyService(
                                null
                        )
                )
                .withMessageContaining(
                        "queryPort"
                );
    }

    @Test
    void shouldRejectNullRequestWithoutQueryingPersistence() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        service.decide(null)
                )
                .withMessageContaining(
                        "request"
                );

        verifyNoInteractions(queryPort);
    }

    @Test
    void shouldGenerateWhenExactVectorDoesNotExist() {
        ScientificFeatureGenerationRequest request =
                request();

        when(
                queryPort.findExact(
                        request.participantId(),
                        request.featureSetVersion(),
                        request.featureCutoffAt()
                )
        ).thenReturn(
                Optional.empty()
        );

        ScientificGenerationDecision decision =
                service.decide(request);

        assertThat(decision.shouldGenerate())
                .isTrue();

        assertThat(decision.shouldReuse())
                .isFalse();

        assertThat(decision.vector())
                .isEmpty();

        verify(queryPort)
                .findExact(
                        request.participantId(),
                        request.featureSetVersion(),
                        request.featureCutoffAt()
                );

        verifyNoMoreInteractions(queryPort);
    }

    @Test
    void shouldReuseWhenExactVectorExists() {
        ScientificFeatureGenerationRequest request =
                request();

        ScientificFeatureVector vector =
                vector();

        when(
                queryPort.findExact(
                        request.participantId(),
                        request.featureSetVersion(),
                        request.featureCutoffAt()
                )
        ).thenReturn(
                Optional.of(vector)
        );

        ScientificGenerationDecision decision =
                service.decide(request);

        assertThat(decision.shouldReuse())
                .isTrue();

        assertThat(decision.shouldGenerate())
                .isFalse();

        assertThat(decision.vector())
                .containsSame(vector);

        verify(queryPort)
                .findExact(
                        request.participantId(),
                        request.featureSetVersion(),
                        request.featureCutoffAt()
                );

        verifyNoMoreInteractions(queryPort);
    }

    @Test
    void shouldUseExactLogicalKeyFromRequest() {
        ScientificFeatureGenerationRequest request =
                request();

        when(
                queryPort.findExact(
                        new ParticipantId(
                                "PARTICIPANT-001"
                        ),
                        new FeatureSetVersion(
                                "FEATURES-V1"
                        ),
                        CUTOFF
                )
        ).thenReturn(
                Optional.empty()
        );

        service.decide(request);

        verify(queryPort)
                .findExact(
                        new ParticipantId(
                                "PARTICIPANT-001"
                        ),
                        new FeatureSetVersion(
                                "FEATURES-V1"
                        ),
                        CUTOFF
                );
    }

    private ScientificFeatureGenerationRequest request() {
        return new ScientificFeatureGenerationRequest(
                new ScientificFeatureVectorId(
                        "VECTOR-IDEMPOTENCY-NEW"
                ),
                new ParticipantId(
                        "PARTICIPANT-001"
                ),
                new FeatureSetVersion(
                        "FEATURES-V1"
                ),
                new GeneratorVersion(
                        "GENERATOR-V1"
                ),
                CUTOFF,
                CUTOFF.plusSeconds(1),
                1,
                new ScientificChecksum(
                        "CHECKSUM-NEW"
                ),
                List.of(feature())
        );
    }

    private ScientificFeatureVector vector() {
        return new ScientificFeatureVector(
                new ScientificFeatureVectorId(
                        "VECTOR-IDEMPOTENCY-EXISTING"
                ),
                new ParticipantId(
                        "PARTICIPANT-001"
                ),
                new FeatureSetVersion(
                        "FEATURES-V1"
                ),
                new GeneratorVersion(
                        "GENERATOR-V1"
                ),
                CUTOFF,
                CUTOFF.plusSeconds(1),
                1,
                new ScientificChecksum(
                        "CHECKSUM-EXISTING"
                ),
                List.of(feature())
        );
    }

    private ScientificFeatureItem feature() {
        return new ScientificFeatureItem(
                "FEATURE-001",
                new FeatureCode(
                        "KOLB_CE"
                ),
                FeatureValue.numeric(
                        25.0
                ),
                "KOLB",
                "ADMIN-001"
        );
    }
}