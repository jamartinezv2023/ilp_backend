package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificChecksum;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ScientificGenerationContextTest {

    @Test
    void shouldCreateEmptyInitialContext() {
        ScientificGenerationContext context =
                ScientificGenerationContext.initial(
                        request()
                );

        assertThat(context.features()).isEmpty();
        assertThat(context.result()).isNull();
        assertThat(context.hasResult()).isFalse();
    }

    @Test
    void shouldDefensivelyCopyFeatures() {
        List<ScientificFeatureItem> features =
                new ArrayList<>();

        features.add(feature());

        ScientificGenerationContext context =
                ScientificGenerationContext
                        .initial(request())
                        .withFeatures(features);

        features.clear();

        assertThat(context.features())
                .containsExactly(feature());

        assertThatExceptionOfType(
                UnsupportedOperationException.class
        ).isThrownBy(() ->
                context.features().add(feature())
        );
    }

    @Test
    void shouldRejectNullRequest() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        ScientificGenerationContext.initial(
                                null
                        )
                )
                .withMessageContaining(
                        "request"
                );
    }

    private ScientificFeatureGenerationRequest request() {
        Instant cutoff =
                Instant.parse(
                        "2026-07-31T20:00:00Z"
                );

        return new ScientificFeatureGenerationRequest(
                new ScientificFeatureVectorId("VECTOR-001"),
                new ParticipantId("STUDENT-001"),
                new FeatureSetVersion("FEATURES-V1"),
                new GeneratorVersion("GENERATOR-V1"),
                cutoff,
                cutoff.plusSeconds(1),
                1,
                new ScientificChecksum("CHECKSUM-001"),
                List.of(feature())
        );
    }

    private ScientificFeatureItem feature() {
        return new ScientificFeatureItem(
                "FEATURE-001",
                new FeatureCode("SEED_FEATURE"),
                FeatureValue.numeric(1.0),
                "TEST",
                "ADMIN-001"
        );
    }
}