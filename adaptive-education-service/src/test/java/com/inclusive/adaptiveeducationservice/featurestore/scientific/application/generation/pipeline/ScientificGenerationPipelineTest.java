package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline.step.ProviderExecutionStep;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline.step.SeedFeaturesStep;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline.step.VectorAssemblyStep;
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
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.provider.ScientificFeatureProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScientificGenerationPipelineTest {

    @Test
    void shouldExecuteCompletePipeline() {
        ScientificFeatureProvider first =
                mock(ScientificFeatureProvider.class);

        ScientificFeatureProvider second =
                mock(ScientificFeatureProvider.class);

        ScientificFeatureGenerationRequest request =
                request();

        ScientificFeatureItem firstFeature =
                feature(
                        "PROVIDED-001",
                        "FIRST_PROVIDER_FEATURE",
                        2.0
                );

        ScientificFeatureItem secondFeature =
                feature(
                        "PROVIDED-002",
                        "SECOND_PROVIDER_FEATURE",
                        3.0
                );

        when(first.provide(request))
                .thenReturn(List.of(firstFeature));

        when(second.provide(request))
                .thenReturn(List.of(secondFeature));

        ScientificGenerationPipeline pipeline =
                new ScientificGenerationPipeline(
                        List.of(
                                new SeedFeaturesStep(),
                                new ProviderExecutionStep(
                                        List.of(first, second)
                                ),
                                new VectorAssemblyStep()
                        )
                );

        ScientificFeatureVector result =
                pipeline.execute(request);

        assertThat(result.items())
                .containsExactlyInAnyOrder(
                        seedFeature(),
                        firstFeature,
                        secondFeature
                );

        assertThat(result.id())
                .isEqualTo(request.vectorId());

        assertThat(result.sourceObservationCount())
                .isEqualTo(
                        request.inputObservationCount()
                );

        var order = inOrder(first, second);

        order.verify(first).provide(request);
        order.verify(second).provide(request);
    }

    @Test
    void shouldPreserveProviderListDefensively() {
        ScientificFeatureProvider provider =
                mock(ScientificFeatureProvider.class);

        List<ScientificFeatureProvider> providers =
                new ArrayList<>();

        providers.add(provider);

        ProviderExecutionStep step =
                new ProviderExecutionStep(providers);

        providers.clear();

        ScientificFeatureGenerationRequest request =
                request();

        when(provider.provide(request))
                .thenReturn(List.of());

        ScientificGenerationContext context =
                new SeedFeaturesStep()
                        .execute(
                                ScientificGenerationContext
                                        .initial(request)
                        );

        step.execute(context);

        var order = inOrder(provider);

        order.verify(provider).provide(request);
    }

    @Test
    void shouldRejectEmptyPipeline() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        new ScientificGenerationPipeline(
                                List.of()
                        )
                )
                .withMessageContaining(
                        "At least one"
                );
    }

    @Test
    void shouldRejectNullRequest() {
        ScientificGenerationPipeline pipeline =
                new ScientificGenerationPipeline(
                        List.of(
                                new VectorAssemblyStep()
                        )
                );

        assertThatNullPointerException()
                .isThrownBy(() ->
                        pipeline.execute(null)
                )
                .withMessageContaining(
                        "request"
                );
    }

    @Test
    void shouldRejectPipelineWithoutAssemblyResult() {
        ScientificGenerationPipeline pipeline =
                new ScientificGenerationPipeline(
                        List.of(
                                new SeedFeaturesStep()
                        )
                );

        assertThatIllegalStateException()
                .isThrownBy(() ->
                        pipeline.execute(request())
                )
                .withMessageContaining(
                        "without a scientific feature vector"
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
                List.of(seedFeature())
        );
    }

    private ScientificFeatureItem seedFeature() {
        return feature(
                "SEED-001",
                "SEED_FEATURE",
                1.0
        );
    }

    private ScientificFeatureItem feature(
            String id,
            String code,
            double value
    ) {
        return new ScientificFeatureItem(
                id,
                new FeatureCode(code),
                FeatureValue.numeric(value),
                "TEST",
                "ADMIN-001"
        );
    }
}