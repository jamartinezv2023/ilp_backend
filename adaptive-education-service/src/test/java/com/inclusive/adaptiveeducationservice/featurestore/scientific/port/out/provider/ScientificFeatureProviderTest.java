package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.provider;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Contract tests for {@link ScientificFeatureProvider}.
 *
 * <p>The test suite verifies that the output port remains a lightweight
 * functional interface that can be implemented without Spring, persistence,
 * transport, or infrastructure dependencies.</p>
 */
class ScientificFeatureProviderTest {

    @Test
    void shouldBeImplementableUsingLambda() {
        ScientificFeatureGenerationRequest request =
                mock(ScientificFeatureGenerationRequest.class);

        ScientificFeatureItem feature =
                numericFeature(
                        "SFI-001",
                        "KOLB_ACTIVE_SCORE",
                        12.0
                );

        ScientificFeatureProvider provider =
                ignoredRequest -> List.of(feature);

        List<ScientificFeatureItem> result =
                provider.provide(request);

        assertThat(result)
                .containsExactly(feature);
    }

    @Test
    void shouldReceiveSameGenerationRequestInstance() {
        ScientificFeatureGenerationRequest request =
                mock(ScientificFeatureGenerationRequest.class);

        AtomicReference<ScientificFeatureGenerationRequest>
                receivedRequest =
                new AtomicReference<>();

        ScientificFeatureProvider provider =
                suppliedRequest -> {
                    receivedRequest.set(suppliedRequest);

                    return List.of(
                            numericFeature(
                                    "SFI-001",
                                    "KOLB_ACTIVE_SCORE",
                                    12.0
                            )
                    );
                };

        provider.provide(request);

        assertThat(receivedRequest.get())
                .isSameAs(request);
    }

    @Test
    void shouldProvideMultipleScientificFeatureItems() {
        ScientificFeatureGenerationRequest request =
                mock(ScientificFeatureGenerationRequest.class);

        ScientificFeatureItem activeScore =
                numericFeature(
                        "SFI-001",
                        "KOLB_ACTIVE_SCORE",
                        12.0
                );

        ScientificFeatureItem reflectiveScore =
                numericFeature(
                        "SFI-002",
                        "KOLB_REFLECTIVE_SCORE",
                        9.0
                );

        ScientificFeatureProvider provider =
                ignoredRequest ->
                        List.of(
                                activeScore,
                                reflectiveScore
                        );

        List<ScientificFeatureItem> result =
                provider.provide(request);

        assertThat(result)
                .containsExactly(
                        activeScore,
                        reflectiveScore
                );
    }

    @Test
    void shouldReturnSameCollectionProducedByImplementation() {
        ScientificFeatureGenerationRequest request =
                mock(ScientificFeatureGenerationRequest.class);

        List<ScientificFeatureItem> suppliedFeatures =
                List.of(
                        numericFeature(
                                "SFI-001",
                                "KOLB_ACTIVE_SCORE",
                                12.0
                        )
                );

        ScientificFeatureProvider provider =
                ignoredRequest -> suppliedFeatures;

        List<ScientificFeatureItem> result =
                provider.provide(request);

        assertThat(result)
                .isSameAs(suppliedFeatures);
    }

    @Test
    void shouldPropagateProviderFailureWithoutWrappingIt() {
        ScientificFeatureGenerationRequest request =
                mock(ScientificFeatureGenerationRequest.class);

        IllegalStateException providerFailure =
                new IllegalStateException(
                        "Scientific feature source unavailable"
                );

        ScientificFeatureProvider provider =
                ignoredRequest -> {
                    throw providerFailure;
                };

        assertThatThrownBy(() ->
                provider.provide(request)
        ).isSameAs(providerFailure);
    }

    private ScientificFeatureItem numericFeature(
            String id,
            String code,
            double value
    ) {
        return new ScientificFeatureItem(
                id,
                new FeatureCode(code),
                FeatureValue.numeric(value),
                "KOLB",
                "ADMINISTRATION-001"
        );
    }
}