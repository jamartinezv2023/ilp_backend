package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation;

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
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ProviderCompositeScientificFeatureGeneratorTest {

    private static final Instant CUTOFF =
            Instant.parse(
                    "2026-07-31T20:00:00Z"
            );

    @Test
    void shouldCombineSeedAndProviderFeaturesInStableOrder() {
        ScientificFeatureProvider firstProvider =
                mock(ScientificFeatureProvider.class);

        ScientificFeatureProvider secondProvider =
                mock(ScientificFeatureProvider.class);

        ScientificFeatureGenerationRequest request =
                request();

        ScientificFeatureItem firstFeature =
                feature(
                        "FEATURE-1",
                        "FIRST_FEATURE",
                        1.0
                );

        ScientificFeatureItem secondFeature =
                feature(
                        "FEATURE-2",
                        "SECOND_FEATURE",
                        2.0
                );

        when(firstProvider.provide(request))
                .thenReturn(List.of(firstFeature));

        when(secondProvider.provide(request))
                .thenReturn(List.of(secondFeature));

        ProviderCompositeScientificFeatureGenerator generator =
                new ProviderCompositeScientificFeatureGenerator(
                        List.of(
                                firstProvider,
                                secondProvider
                        )
                );

        ScientificFeatureVector result =
                generator.generate(request);

        assertThat(result.items())
                .containsExactlyInAnyOrder(
                        seedFeature(),
                        firstFeature,
                        secondFeature
                );

        assertThat(result.id())
                .isEqualTo(request.vectorId());

        assertThat(result.participantId())
                .isEqualTo(request.participantId());

        assertThat(result.featureSetVersion())
                .isEqualTo(
                        request.featureSetVersion()
                );

        assertThat(result.generatorVersion())
                .isEqualTo(
                        request.generatorVersion()
                );

        assertThat(result.sourceObservationCount())
                .isEqualTo(
                        request.inputObservationCount()
                );

        var executionOrder =
                inOrder(
                        firstProvider,
                        secondProvider
                );

        executionOrder
                .verify(firstProvider)
                .provide(request);

        executionOrder
                .verify(secondProvider)
                .provide(request);
    }

    @Test
    void shouldCreateVectorUsingOnlySeedFeaturesWhenProvidersAreEmpty() {
        ScientificFeatureGenerationRequest request =
                request();

        ProviderCompositeScientificFeatureGenerator generator =
                new ProviderCompositeScientificFeatureGenerator(
                        List.of()
                );

        ScientificFeatureVector result =
                generator.generate(request);

        assertThat(result.items())
                .containsExactly(
                        seedFeature()
                );

        assertThat(result.featureCount())
                .isEqualTo(1);
    }

    @Test
    void shouldDefensivelyCopyProviderList() {
        ScientificFeatureProvider provider =
                mock(ScientificFeatureProvider.class);

        List<ScientificFeatureProvider> mutableProviders =
                new ArrayList<>();

        mutableProviders.add(provider);

        ProviderCompositeScientificFeatureGenerator generator =
                new ProviderCompositeScientificFeatureGenerator(
                        mutableProviders
                );

        mutableProviders.clear();

        ScientificFeatureGenerationRequest request =
                request();

        when(provider.provide(request))
                .thenReturn(List.of());

        generator.generate(request);

        var executionOrder = inOrder(provider);

        executionOrder
                .verify(provider)
                .provide(request);
    }

    @Test
    void shouldRejectNullProviderList() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new ProviderCompositeScientificFeatureGenerator(
                                null
                        )
                )
                .withMessage(
                        "providers must not be null"
                );
    }

    @Test
    void shouldRejectNullProviderElement() {
        List<ScientificFeatureProvider> providers =
                new ArrayList<>();

        providers.add(null);

        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        new ProviderCompositeScientificFeatureGenerator(
                                providers
                        )
                )
                .withMessageContaining(
                        "null elements"
                );
    }

    @Test
    void shouldRejectNullRequestWithoutCallingProviders() {
        ScientificFeatureProvider provider =
                mock(ScientificFeatureProvider.class);

        ProviderCompositeScientificFeatureGenerator generator =
                new ProviderCompositeScientificFeatureGenerator(
                        List.of(provider)
                );

        assertThatNullPointerException()
                .isThrownBy(() ->
                        generator.generate(null)
                )
                .withMessage(
                        "request must not be null"
                );

        verifyNoInteractions(provider);
    }

    @Test
    void shouldRejectNullProviderResult() {
        ScientificFeatureProvider provider =
                mock(ScientificFeatureProvider.class);

        ScientificFeatureGenerationRequest request =
                request();

        when(provider.provide(request))
                .thenReturn(null);

        ProviderCompositeScientificFeatureGenerator generator =
                new ProviderCompositeScientificFeatureGenerator(
                        List.of(provider)
                );

        assertThatNullPointerException()
                .isThrownBy(() ->
                        generator.generate(request)
                )
                .withMessageContaining(
                        "provider result"
                );
    }

    @Test
    void shouldRejectNullFeatureReturnedByProvider() {
        ScientificFeatureProvider provider =
                mock(ScientificFeatureProvider.class);

        ScientificFeatureGenerationRequest request =
                request();

        List<ScientificFeatureItem> invalidFeatures =
                new ArrayList<>();

        invalidFeatures.add(null);

        when(provider.provide(request))
                .thenReturn(invalidFeatures);

        ProviderCompositeScientificFeatureGenerator generator =
                new ProviderCompositeScientificFeatureGenerator(
                        List.of(provider)
                );

        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        generator.generate(request)
                )
                .withMessageContaining(
                        "null elements"
                );
    }

    private ScientificFeatureGenerationRequest request() {
        return new ScientificFeatureGenerationRequest(
                new ScientificFeatureVectorId(
                        "VECTOR-COMPOSITE-001"
                ),
                new ParticipantId(
                        "STUDENT-001"
                ),
                new FeatureSetVersion(
                        "SCIENTIFIC-FEATURES-V1"
                ),
                new GeneratorVersion(
                        "COMPOSITE-GENERATOR-V1"
                ),
                CUTOFF,
                CUTOFF.plusSeconds(10),
                1,
                new ScientificChecksum(
                        "sha256:composite-checksum"
                ),
                List.of(seedFeature())
        );
    }

    private ScientificFeatureItem seedFeature() {
        return feature(
                "SEED-001",
                "SEED_FEATURE",
                0.0
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
                "TEST-ADMINISTRATION"
        );
    }
}