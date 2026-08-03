package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.orchestration;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.idempotency.ScientificGenerationDecision;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.idempotency.ScientificGenerationIdempotencyService;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureGenerationRun;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureGenerationRunId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.ScientificFeatureGenerator;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureGenerationRunPersistencePort;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureVectorPersistencePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class TransactionalScientificFeatureGenerationService {

    private static final String RUN_ID_PREFIX =
            "SFGR-";

    private final ScientificGenerationIdempotencyService
            idempotencyService;

    private final ScientificFeatureGenerator generator;

    private final ScientificFeatureVectorPersistencePort
            vectorPersistencePort;

    private final ScientificFeatureGenerationRunPersistencePort
            runPersistencePort;

    private final Clock clock;

    private final Supplier<ScientificFeatureGenerationRunId>
            runIdSupplier;

    @Autowired
    public TransactionalScientificFeatureGenerationService(
            ScientificGenerationIdempotencyService idempotencyService,
            ScientificFeatureGenerator generator,
            ScientificFeatureVectorPersistencePort
                    vectorPersistencePort,
            ScientificFeatureGenerationRunPersistencePort
                    runPersistencePort
    ) {
        this(
                idempotencyService,
                generator,
                vectorPersistencePort,
                runPersistencePort,
                Clock.systemUTC(),
                TransactionalScientificFeatureGenerationService
                        ::newRunId
        );
    }

    TransactionalScientificFeatureGenerationService(
            ScientificGenerationIdempotencyService idempotencyService,
            ScientificFeatureGenerator generator,
            ScientificFeatureVectorPersistencePort
                    vectorPersistencePort,
            ScientificFeatureGenerationRunPersistencePort
                    runPersistencePort,
            Clock clock,
            Supplier<ScientificFeatureGenerationRunId>
                    runIdSupplier
    ) {
        this.idempotencyService =
                Objects.requireNonNull(
                        idempotencyService,
                        "idempotencyService is required"
                );

        this.generator =
                Objects.requireNonNull(
                        generator,
                        "generator is required"
                );

        this.vectorPersistencePort =
                Objects.requireNonNull(
                        vectorPersistencePort,
                        "vectorPersistencePort is required"
                );

        this.runPersistencePort =
                Objects.requireNonNull(
                        runPersistencePort,
                        "runPersistencePort is required"
                );

        this.clock =
                Objects.requireNonNull(
                        clock,
                        "clock is required"
                );

        this.runIdSupplier =
                Objects.requireNonNull(
                        runIdSupplier,
                        "runIdSupplier is required"
                );
    }

    @Transactional
    public ScientificFeatureVector generate(
            ScientificFeatureGenerationRequest request
    ) {
        Objects.requireNonNull(
                request,
                "request is required"
        );

        ScientificGenerationDecision decision =
                idempotencyService.decide(request);

        if (decision.shouldReuse()) {
            return decision.vector()
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Reuse decision requires "
                                            + "an existing vector"
                            )
                    );
        }

        Instant startedAt =
                clock.instant();

        ScientificFeatureGenerationRunId runId =
                Objects.requireNonNull(
                        runIdSupplier.get(),
                        "runIdSupplier result must not be null"
                );

        ScientificFeatureGenerationRun startedRun =
                ScientificFeatureGenerationRun.start(
                        runId,
                        request.participantId(),
                        request.featureSetVersion(),
                        request.generatorVersion(),
                        request.featureCutoffAt(),
                        startedAt,
                        request.inputObservationCount()
                );

        ScientificFeatureGenerationRun persistedStartedRun =
                Objects.requireNonNull(
                        runPersistencePort.save(startedRun),
                        "persisted started run must not be null"
                );

        ScientificFeatureVector generatedVector =
                Objects.requireNonNull(
                        generator.generate(request),
                        "generated vector must not be null"
                );

        ScientificFeatureVector persistedVector =
                Objects.requireNonNull(
                        vectorPersistencePort.save(
                                generatedVector
                        ),
                        "persisted vector must not be null"
                );

        ScientificFeatureGenerationRun completedRun =
                persistedStartedRun.complete(
                        persistedVector.id(),
                        clock.instant()
                );

        Objects.requireNonNull(
                runPersistencePort.save(completedRun),
                "persisted completed run must not be null"
        );

        return persistedVector;
    }

    private static ScientificFeatureGenerationRunId
    newRunId() {
        return new ScientificFeatureGenerationRunId(
                RUN_ID_PREFIX + UUID.randomUUID()
        );
    }
}