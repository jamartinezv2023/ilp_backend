package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureGenerationRunId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScientificFeatureGenerationRunTest {

    private static final Instant CUTOFF =
            Instant.parse(
                    "2026-07-27T10:00:00Z"
            );

    private static final Instant STARTED_AT =
            Instant.parse(
                    "2026-07-27T10:00:01Z"
            );

    @Test
    void shouldStartGenerationRun() {
        ScientificFeatureGenerationRun run =
                startedRun();

        assertThat(run.id().value())
                .isEqualTo("SFGR-001");

        assertThat(run.status())
                .isEqualTo(
                        ScientificFeatureGenerationStatus.STARTED
                );

        assertThat(run.isStarted())
                .isTrue();

        assertThat(run.isCompleted())
                .isFalse();

        assertThat(run.isFailed())
                .isFalse();

        assertThat(run.completedAt())
                .isNull();

        assertThat(run.featureVectorId())
                .isNull();

        assertThat(run.errorMessage())
                .isNull();

        assertThat(run.duration())
                .isEmpty();
    }

    @Test
    void shouldCompleteStartedRun() {
        Instant completedAt =
                STARTED_AT.plusSeconds(5);

        ScientificFeatureGenerationRun completed =
                startedRun().complete(
                        new ScientificFeatureVectorId(
                                "SFV-001"
                        ),
                        completedAt
                );

        assertThat(completed.isCompleted())
                .isTrue();

        assertThat(completed.status())
                .isEqualTo(
                        ScientificFeatureGenerationStatus.COMPLETED
                );

        assertThat(
                completed.featureVectorId().value()
        ).isEqualTo("SFV-001");

        assertThat(completed.completedAt())
                .isEqualTo(completedAt);

        assertThat(completed.errorMessage())
                .isNull();

        assertThat(completed.duration())
                .contains(
                        Duration.ofSeconds(5)
                );
    }

    @Test
    void shouldFailStartedRun() {
        Instant completedAt =
                STARTED_AT.plusSeconds(3);

        ScientificFeatureGenerationRun failed =
                startedRun().fail(
                        "  No scientific observations available  ",
                        completedAt
                );

        assertThat(failed.isFailed())
                .isTrue();

        assertThat(failed.status())
                .isEqualTo(
                        ScientificFeatureGenerationStatus.FAILED
                );

        assertThat(failed.errorMessage())
                .isEqualTo(
                        "No scientific observations available"
                );

        assertThat(failed.featureVectorId())
                .isNull();

        assertThat(failed.duration())
                .contains(
                        Duration.ofSeconds(3)
                );
    }

    @Test
    void shouldPreserveStartedRunWhenTransitioning() {
        ScientificFeatureGenerationRun started =
                startedRun();

        ScientificFeatureGenerationRun completed =
                started.complete(
                        new ScientificFeatureVectorId(
                                "SFV-001"
                        ),
                        STARTED_AT.plusSeconds(2)
                );

        assertThat(started.isStarted())
                .isTrue();

        assertThat(started.completedAt())
                .isNull();

        assertThat(completed)
                .isNotSameAs(started);

        assertThat(completed.isCompleted())
                .isTrue();
    }

    @Test
    void shouldRejectCompletionBeforeStart() {
        assertThatThrownBy(() ->
                startedRun().complete(
                        new ScientificFeatureVectorId(
                                "SFV-001"
                        ),
                        STARTED_AT.minusSeconds(1)
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "completedAt"
                );
    }

    @Test
    void shouldRejectBlankFailureReason() {
        assertThatThrownBy(() ->
                startedRun().fail(
                        " ",
                        STARTED_AT.plusSeconds(1)
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "errorMessage"
                );
    }

    @Test
    void shouldRejectNegativeInputObservationCount() {
        assertThatThrownBy(() ->
                ScientificFeatureGenerationRun.start(
                        new ScientificFeatureGenerationRunId(
                                "SFGR-001"
                        ),
                        new ParticipantId("ST-001"),
                        new FeatureSetVersion(
                                "ILP_SCIENTIFIC_BASELINE_V1"
                        ),
                        new GeneratorVersion(
                                "SCIENTIFIC_FEATURE_GENERATOR_V1"
                        ),
                        CUTOFF,
                        STARTED_AT,
                        -1
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "inputObservationCount"
                );
    }

    @Test
    void shouldRejectSecondTransitionAfterCompletion() {
        ScientificFeatureGenerationRun completed =
                startedRun().complete(
                        new ScientificFeatureVectorId(
                                "SFV-001"
                        ),
                        STARTED_AT.plusSeconds(1)
                );

        assertThatThrownBy(() ->
                completed.fail(
                        "Unexpected error",
                        STARTED_AT.plusSeconds(2)
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessageContaining(
                        "STARTED"
                );
    }

    @Test
    void shouldRejectSecondTransitionAfterFailure() {
        ScientificFeatureGenerationRun failed =
                startedRun().fail(
                        "Generation failed",
                        STARTED_AT.plusSeconds(1)
                );

        assertThatThrownBy(() ->
                failed.complete(
                        new ScientificFeatureVectorId(
                                "SFV-001"
                        ),
                        STARTED_AT.plusSeconds(2)
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessageContaining(
                        "STARTED"
                );
    }

    @Test
    void shouldAcceptZeroInputObservations() {
        ScientificFeatureGenerationRun run =
                ScientificFeatureGenerationRun.start(
                        new ScientificFeatureGenerationRunId(
                                "SFGR-ZERO"
                        ),
                        new ParticipantId("ST-001"),
                        new FeatureSetVersion(
                                "ILP_SCIENTIFIC_BASELINE_V1"
                        ),
                        new GeneratorVersion(
                                "SCIENTIFIC_FEATURE_GENERATOR_V1"
                        ),
                        CUTOFF,
                        STARTED_AT,
                        0
                );

        assertThat(run.inputObservationCount())
                .isZero();
    }

    private ScientificFeatureGenerationRun startedRun() {
        return ScientificFeatureGenerationRun.start(
                new ScientificFeatureGenerationRunId(
                        "SFGR-001"
                ),
                new ParticipantId("ST-001"),
                new FeatureSetVersion(
                        "ILP_SCIENTIFIC_BASELINE_V1"
                ),
                new GeneratorVersion(
                        "SCIENTIFIC_FEATURE_GENERATOR_V1"
                ),
                CUTOFF,
                STARTED_AT,
                2
        );
    }
}