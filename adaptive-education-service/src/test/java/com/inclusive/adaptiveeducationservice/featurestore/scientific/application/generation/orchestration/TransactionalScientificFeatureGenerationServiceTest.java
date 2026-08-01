package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.orchestration;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.idempotency.ScientificGenerationIdempotencyService;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureGenerationRun;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificChecksum;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureGenerationRunId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.ScientificFeatureGenerator;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureGenerationRunPersistencePort;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureVectorPersistencePort;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureVectorQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TransactionalScientificFeatureGenerationServiceTest {

    private static final Instant TRANSACTION_TIME =
            Instant.parse(
                    "2026-07-31T23:00:00Z"
            );

    private static final ScientificFeatureGenerationRunId RUN_ID =
            new ScientificFeatureGenerationRunId(
                    "RUN-TX-001"
            );

    private ScientificFeatureVectorQueryPort queryPort;
    private ScientificFeatureGenerator generator;

    private ScientificFeatureVectorPersistencePort
            vectorPersistencePort;

    private ScientificFeatureGenerationRunPersistencePort
            runPersistencePort;

    private TransactionalScientificFeatureGenerationService
            service;

    @BeforeEach
    void setUp() {
        queryPort =
                mock(
                        ScientificFeatureVectorQueryPort.class
                );

        generator =
                mock(
                        ScientificFeatureGenerator.class
                );

        vectorPersistencePort =
                mock(
                        ScientificFeatureVectorPersistencePort.class
                );

        runPersistencePort =
                mock(
                        ScientificFeatureGenerationRunPersistencePort.class
                );

        ScientificGenerationIdempotencyService
                idempotencyService =
                new ScientificGenerationIdempotencyService(
                        queryPort
                );

        service =
                new TransactionalScientificFeatureGenerationService(
                        idempotencyService,
                        generator,
                        vectorPersistencePort,
                        runPersistencePort,
                        Clock.fixed(
                                TRANSACTION_TIME,
                                ZoneOffset.UTC
                        ),
                        () -> RUN_ID
                );
    }

    @Test
    void shouldReuseExactVectorWithoutGeneratingOrPersisting() {
        ScientificFeatureGenerationRequest request =
                request();

        ScientificFeatureVector existingVector =
                vector(
                        "VECTOR-EXISTING"
                );

        when(
                queryPort.findExact(
                        request.participantId(),
                        request.featureSetVersion(),
                        request.featureCutoffAt()
                )
        ).thenReturn(
                Optional.of(existingVector)
        );

        ScientificFeatureVector result =
                service.generate(request);

        assertThat(result)
                .isSameAs(existingVector);

        verify(queryPort)
                .findExact(
                        request.participantId(),
                        request.featureSetVersion(),
                        request.featureCutoffAt()
                );

        verifyNoInteractions(
                generator,
                vectorPersistencePort,
                runPersistencePort
        );
    }

    @Test
    void shouldGeneratePersistAndCompleteRun() {
        ScientificFeatureGenerationRequest request =
                request();

        ScientificFeatureVector generatedVector =
                vector(
                        "VECTOR-GENERATED"
                );

        ScientificFeatureVector persistedVector =
                vector(
                        "VECTOR-PERSISTED"
                );

        when(
                queryPort.findExact(
                        request.participantId(),
                        request.featureSetVersion(),
                        request.featureCutoffAt()
                )
        ).thenReturn(
                Optional.empty()
        );

        when(
                runPersistencePort.save(
                        any(
                                ScientificFeatureGenerationRun.class
                        )
                )
        ).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        when(generator.generate(request))
                .thenReturn(generatedVector);

        when(
                vectorPersistencePort.save(
                        generatedVector
                )
        ).thenReturn(
                persistedVector
        );

        ScientificFeatureVector result =
                service.generate(request);

        assertThat(result)
                .isSameAs(persistedVector);

        ArgumentCaptor<ScientificFeatureGenerationRun>
                runCaptor =
                ArgumentCaptor.forClass(
                        ScientificFeatureGenerationRun.class
                );

        verify(runPersistencePort, times(2))
                .save(runCaptor.capture());

        List<ScientificFeatureGenerationRun> savedRuns =
                runCaptor.getAllValues();

        ScientificFeatureGenerationRun startedRun =
                savedRuns.get(0);

        ScientificFeatureGenerationRun completedRun =
                savedRuns.get(1);

        assertThat(startedRun.isStarted())
                .isTrue();

        assertThat(startedRun.id())
                .isEqualTo(RUN_ID);

        assertThat(startedRun.participantId())
                .isEqualTo(
                        request.participantId()
                );

        assertThat(startedRun.inputObservationCount())
                .isEqualTo(
                        request.inputObservationCount()
                );

        assertThat(completedRun.isCompleted())
                .isTrue();

        assertThat(completedRun.featureVectorId())
                .isEqualTo(
                        persistedVector.id()
                );

        assertThat(completedRun.completedAt())
                .isEqualTo(
                        TRANSACTION_TIME
                );

        var order =
                inOrder(
                        queryPort,
                        runPersistencePort,
                        generator,
                        vectorPersistencePort
                );

        order.verify(queryPort)
                .findExact(
                        request.participantId(),
                        request.featureSetVersion(),
                        request.featureCutoffAt()
                );

        order.verify(runPersistencePort)
                .save(
                        any(
                                ScientificFeatureGenerationRun.class
                        )
                );

        order.verify(generator)
                .generate(request);

        order.verify(vectorPersistencePort)
                .save(generatedVector);

        order.verify(runPersistencePort)
                .save(
                        any(
                                ScientificFeatureGenerationRun.class
                        )
                );
    }

    @Test
    void shouldPropagateGeneratorFailureWithoutSavingVectorOrCompletedRun() {
        ScientificFeatureGenerationRequest request =
                request();

        IllegalStateException generationFailure =
                new IllegalStateException(
                        "scientific generation failed"
                );

        when(
                queryPort.findExact(
                        request.participantId(),
                        request.featureSetVersion(),
                        request.featureCutoffAt()
                )
        ).thenReturn(
                Optional.empty()
        );

        when(
                runPersistencePort.save(
                        any(
                                ScientificFeatureGenerationRun.class
                        )
                )
        ).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        when(generator.generate(request))
                .thenThrow(generationFailure);

        assertThatThrownBy(() ->
                service.generate(request)
        )
                .isSameAs(generationFailure);

        verify(runPersistencePort, times(1))
                .save(
                        any(
                                ScientificFeatureGenerationRun.class
                        )
                );

        verify(vectorPersistencePort, never())
                .save(
                        any(
                                ScientificFeatureVector.class
                        )
                );
    }

    @Test
    void shouldRejectNullRequestBeforeAnyInteraction() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        service.generate(null)
                )
                .withMessageContaining(
                        "request"
                );

        verifyNoInteractions(
                queryPort,
                generator,
                vectorPersistencePort,
                runPersistencePort
        );
    }

    @Test
    void shouldDeclareTransactionalBoundary()
            throws NoSuchMethodException {
        Method generateMethod =
                TransactionalScientificFeatureGenerationService
                        .class
                        .getMethod(
                                "generate",
                                ScientificFeatureGenerationRequest
                                        .class
                        );

        assertThat(
                generateMethod.isAnnotationPresent(
                        Transactional.class
                )
        ).isTrue();
    }

    private ScientificFeatureGenerationRequest request() {
        return new ScientificFeatureGenerationRequest(
                new ScientificFeatureVectorId(
                        "VECTOR-REQUEST"
                ),
                new ParticipantId(
                        "PARTICIPANT-TX-001"
                ),
                new FeatureSetVersion(
                        "FEATURES-V1"
                ),
                new GeneratorVersion(
                        "GENERATOR-V1"
                ),
                TRANSACTION_TIME.minusSeconds(60),
                TRANSACTION_TIME,
                1,
                new ScientificChecksum(
                        "CHECKSUM-TX-001"
                ),
                List.of(feature())
        );
    }

    private ScientificFeatureVector vector(
            String vectorId
    ) {
        return new ScientificFeatureVector(
                new ScientificFeatureVectorId(
                        vectorId
                ),
                new ParticipantId(
                        "PARTICIPANT-TX-001"
                ),
                new FeatureSetVersion(
                        "FEATURES-V1"
                ),
                new GeneratorVersion(
                        "GENERATOR-V1"
                ),
                TRANSACTION_TIME.minusSeconds(60),
                TRANSACTION_TIME,
                1,
                new ScientificChecksum(
                        "CHECKSUM-TX-001"
                ),
                List.of(feature())
        );
    }

    private ScientificFeatureItem feature() {
        return new ScientificFeatureItem(
                "FEATURE-TX-001",
                new FeatureCode(
                        "KOLB_CE"
                ),
                FeatureValue.numeric(
                        25.0
                ),
                "KOLB",
                "ADMIN-TX-001"
        );
    }
}