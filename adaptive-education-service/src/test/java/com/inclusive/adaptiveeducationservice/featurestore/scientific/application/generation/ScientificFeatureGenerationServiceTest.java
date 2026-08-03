package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.ScientificFeatureGenerator;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ScientificFeatureGenerationService}.
 *
 * <p>The test suite verifies constructor validation, input validation,
 * delegation semantics, result identity, and exception propagation without
 * starting Spring or accessing persistence infrastructure.</p>
 */
@ExtendWith(MockitoExtension.class)
class ScientificFeatureGenerationServiceTest {

    @Mock
    private ScientificFeatureGenerator generator;

    @Mock
    private ScientificFeatureGenerationRequest request;

    @Mock
    private ScientificFeatureVector generatedVector;

    private ScientificFeatureGenerationService service;

    @BeforeEach
    void setUp() {
        service =
                new ScientificFeatureGenerationService(
                        generator
                );
    }

    @Test
    void shouldRejectNullGenerator() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new ScientificFeatureGenerationService(
                                null
                        )
                )
                .withMessage(
                        "generator must not be null"
                );
    }

    @Test
    void shouldRejectNullRequestWithoutCallingGenerator() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        service.generate(null)
                )
                .withMessage(
                        "request must not be null"
                );

        verifyNoInteractions(generator);
    }

    @Test
    void shouldDelegateGenerationAndReturnSameVector() {
        when(generator.generate(request))
                .thenReturn(generatedVector);

        ScientificFeatureVector result =
                service.generate(request);

        assertThat(result)
                .isSameAs(generatedVector);

        verify(generator)
                .generate(request);

        verifyNoMoreInteractions(generator);
    }

    @Test
    void shouldPropagateGeneratorFailureWithoutWrappingIt() {
        IllegalStateException generationFailure =
                new IllegalStateException(
                        "Scientific generation failed"
                );

        when(generator.generate(request))
                .thenThrow(generationFailure);

        assertThatThrownBy(() ->
                service.generate(request)
        )
                .isSameAs(generationFailure);

        verify(generator)
                .generate(request);

        verifyNoMoreInteractions(generator);
    }
}
