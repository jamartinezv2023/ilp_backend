package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.orchestration;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificChecksum;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.ScientificFeatureGenerator;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureVectorPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class TransactionalScientificFeatureGenerationIntegrationTest {

    private static final Instant FEATURE_CUTOFF_AT =
            Instant.parse(
                    "2026-07-31T22:00:00Z"
            );

    private static final Instant GENERATED_AT =
            Instant.parse(
                    "2026-07-31T22:00:10Z"
            );

    private static final ParticipantId PARTICIPANT_ID =
            new ParticipantId(
                    "PARTICIPANT-INTEGRATION-001"
            );

    private static final FeatureSetVersion FEATURE_SET_VERSION =
            new FeatureSetVersion(
                    "SCIENTIFIC-FEATURES-INTEGRATION-V1"
            );

    private static final GeneratorVersion GENERATOR_VERSION =
            new GeneratorVersion(
                    "INTEGRATION-GENERATOR-V1"
            );

    @Autowired
    private TransactionalScientificFeatureGenerationService
            service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private ScientificFeatureGenerator generator;

    @SpyBean
    private ScientificFeatureVectorPersistencePort
            vectorPersistencePort;

    @BeforeEach
    void cleanDatabaseAndMocks() {
        reset(generator);

        clearInvocations(
                vectorPersistencePort
        );

        jdbcTemplate.update(
                "DELETE FROM scientific_feature_generation_runs"
        );

        jdbcTemplate.update(
                "DELETE FROM scientific_feature_items"
        );

        jdbcTemplate.update(
                "DELETE FROM scientific_feature_vectors"
        );
    }

    @Test
    void shouldCommitVectorItemsAndCompletedRun() {
        ScientificFeatureGenerationRequest request =
                request(
                        "VECTOR-INTEGRATION-SUCCESS"
                );

        ScientificFeatureVector generatedVector =
                vector(
                        "VECTOR-INTEGRATION-SUCCESS"
                );

        when(generator.generate(request))
                .thenReturn(generatedVector);

        ScientificFeatureVector result =
                service.generate(request);

        assertThat(result.id())
                .isEqualTo(
                        generatedVector.id()
                );

        assertThat(countRows(
                "scientific_feature_vectors"
        )).isEqualTo(1);

        assertThat(countRows(
                "scientific_feature_items"
        )).isEqualTo(
                generatedVector.featureCount()
        );

        assertThat(countRows(
                "scientific_feature_generation_runs"
        )).isEqualTo(1);

        assertThat(countRunsByStatus("COMPLETED"))
                .isEqualTo(1);

        assertThat(countRunsByStatus("STARTED"))
                .isZero();

        assertThat(
                countCompletedRunsForVector(
                        generatedVector.id().value()
                )
        ).isEqualTo(1);

        verify(generator, times(1))
                .generate(request);
    }

    @Test
    void shouldRollbackStartedRunWhenGeneratorFails() {
        ScientificFeatureGenerationRequest request =
                request(
                        "VECTOR-INTEGRATION-GENERATOR-FAILURE"
                );

        IllegalStateException generationFailure =
                new IllegalStateException(
                        "forced generator failure"
                );

        when(generator.generate(request))
                .thenThrow(generationFailure);

        assertThatThrownBy(() ->
                service.generate(request)
        )
                .isSameAs(generationFailure);

        assertScientificTablesAreEmpty();

        verify(generator, times(1))
                .generate(request);
    }

    @Test
    void shouldRollbackStartedRunWhenVectorPersistenceFails() {
        ScientificFeatureGenerationRequest request =
                request(
                        "VECTOR-INTEGRATION-PERSISTENCE-FAILURE"
                );

        ScientificFeatureVector generatedVector =
                vector(
                        "VECTOR-INTEGRATION-PERSISTENCE-FAILURE"
                );

        IllegalStateException persistenceFailure =
                new IllegalStateException(
                        "forced vector persistence failure"
                );

        when(generator.generate(request))
                .thenReturn(generatedVector);

        doThrow(persistenceFailure)
                .when(vectorPersistencePort)
                .save(generatedVector);

        assertThatThrownBy(() ->
                service.generate(request)
        )
                .isSameAs(persistenceFailure);

        assertScientificTablesAreEmpty();

        verify(generator, times(1))
                .generate(request);
    }

    @Test
    void shouldReuseExactVectorWithoutCreatingAnotherRun() {
        ScientificFeatureGenerationRequest firstRequest =
                request(
                        "VECTOR-INTEGRATION-IDEMPOTENT"
                );

        ScientificFeatureVector generatedVector =
                vector(
                        "VECTOR-INTEGRATION-IDEMPOTENT"
                );

        when(generator.generate(firstRequest))
                .thenReturn(generatedVector);

        ScientificFeatureVector firstResult =
                service.generate(firstRequest);

        ScientificFeatureGenerationRequest repeatedRequest =
                request(
                        "VECTOR-IGNORED-BY-IDEMPOTENCY"
                );

        ScientificFeatureVector secondResult =
                service.generate(repeatedRequest);

        assertThat(secondResult.id())
                .isEqualTo(
                        firstResult.id()
                );

        assertThat(countRows(
                "scientific_feature_vectors"
        )).isEqualTo(1);

        assertThat(countRows(
                "scientific_feature_generation_runs"
        )).isEqualTo(1);

        assertThat(countRunsByStatus("COMPLETED"))
                .isEqualTo(1);

        verify(generator, times(1))
                .generate(firstRequest);
    }

    private ScientificFeatureGenerationRequest request(
            String requestedVectorId
    ) {
        return new ScientificFeatureGenerationRequest(
                new ScientificFeatureVectorId(
                        requestedVectorId
                ),
                PARTICIPANT_ID,
                FEATURE_SET_VERSION,
                GENERATOR_VERSION,
                FEATURE_CUTOFF_AT,
                GENERATED_AT,
                1,
                new ScientificChecksum(
                        "CHECKSUM-INTEGRATION-001"
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
                PARTICIPANT_ID,
                FEATURE_SET_VERSION,
                GENERATOR_VERSION,
                FEATURE_CUTOFF_AT,
                GENERATED_AT,
                1,
                new ScientificChecksum(
                        "CHECKSUM-INTEGRATION-001"
                ),
                List.of(feature())
        );
    }

    private ScientificFeatureItem feature() {
        return new ScientificFeatureItem(
                "FEATURE-INTEGRATION-001",
                new FeatureCode(
                        "KOLB_CE"
                ),
                FeatureValue.numeric(
                        25.0
                ),
                "INTEGRATION_TEST",
                "ADMIN-INTEGRATION-001"
        );
    }

    private int countRows(
            String tableName
    ) {
        Integer count =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM "
                                + tableName,
                        Integer.class
                );

        return count == null
                ? 0
                : count;
    }

    private int countRunsByStatus(
            String status
    ) {
        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM scientific_feature_generation_runs
                        WHERE status = ?
                        """,
                        Integer.class,
                        status
                );

        return count == null
                ? 0
                : count;
    }

    private int countCompletedRunsForVector(
            String featureVectorId
    ) {
        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM scientific_feature_generation_runs
                        WHERE status = 'COMPLETED'
                          AND feature_vector_id = ?
                        """,
                        Integer.class,
                        featureVectorId
                );

        return count == null
                ? 0
                : count;
    }

    private void assertScientificTablesAreEmpty() {
        assertThat(countRows(
                "scientific_feature_generation_runs"
        )).isZero();

        assertThat(countRows(
                "scientific_feature_items"
        )).isZero();

        assertThat(countRows(
                "scientific_feature_vectors"
        )).isZero();
    }
}