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
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ScientificGenerationDecisionTest {

    @Test
    void shouldCreateGenerateNewDecision() {
        ScientificGenerationDecision decision =
                ScientificGenerationDecision.generateNew();

        assertThat(decision.shouldGenerate())
                .isTrue();

        assertThat(decision.shouldReuse())
                .isFalse();

        assertThat(decision.vector())
                .isEmpty();
    }

    @Test
    void shouldCreateReuseDecision() {
        ScientificFeatureVector vector =
                vector();

        ScientificGenerationDecision decision =
                ScientificGenerationDecision.reuse(
                        vector
                );

        assertThat(decision.shouldReuse())
                .isTrue();

        assertThat(decision.shouldGenerate())
                .isFalse();

        assertThat(decision.vector())
                .containsSame(vector);
    }

    @Test
    void shouldRejectNullVectorWhenReusing() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        ScientificGenerationDecision.reuse(
                                null
                        )
                )
                .withMessageContaining(
                        "vector"
                );
    }

    @Test
    void shouldRejectReuseWithoutExistingVector() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        new ScientificGenerationDecision(
                                ScientificGenerationDecision.Action.REUSE_EXISTING,
                                null
                        )
                )
                .withMessageContaining(
                        "existingVector"
                );
    }

    @Test
    void shouldRejectGenerateNewWithExistingVector() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        new ScientificGenerationDecision(
                                ScientificGenerationDecision.Action.GENERATE_NEW,
                                vector()
                        )
                )
                .withMessageContaining(
                        "must be null"
                );
    }

    private ScientificFeatureVector vector() {
        Instant cutoff =
                Instant.parse(
                        "2026-07-31T21:00:00Z"
                );

        return new ScientificFeatureVector(
                new ScientificFeatureVectorId(
                        "VECTOR-IDEMPOTENCY-001"
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
                cutoff,
                cutoff.plusSeconds(1),
                1,
                new ScientificChecksum(
                        "CHECKSUM-001"
                ),
                List.of(
                        new ScientificFeatureItem(
                                "FEATURE-001",
                                new FeatureCode(
                                        "KOLB_CE"
                                ),
                                FeatureValue.numeric(
                                        25.0
                                ),
                                "KOLB",
                                "ADMIN-001"
                        )
                )
        );
    }
}