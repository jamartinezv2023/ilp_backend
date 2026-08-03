package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.ScientificFeatureGenerator;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;

import java.util.Objects;

/**
 * Application service responsible for coordinating scientific feature
 * generation.
 *
 * <p>This service deliberately contains no instrument-specific extraction,
 * persistence, transaction, framework, or transport logic. Its current
 * responsibility is to validate the application input and delegate feature
 * generation to the configured {@link ScientificFeatureGenerator} port.</p>
 */
public final class ScientificFeatureGenerationService {

    private final ScientificFeatureGenerator generator;

    /**
     * Creates the scientific feature generation application service.
     *
     * @param generator scientific feature generator port
     * @throws NullPointerException when {@code generator} is {@code null}
     */
    public ScientificFeatureGenerationService(
            ScientificFeatureGenerator generator
    ) {
        this.generator = Objects.requireNonNull(
                generator,
                "generator must not be null"
        );
    }

    /**
     * Generates a scientific feature vector from a validated generation
     * request.
     *
     * @param request scientific feature generation request
     * @return the vector returned by the configured generator
     * @throws NullPointerException when {@code request} is {@code null}
     */
    public ScientificFeatureVector generate(
            ScientificFeatureGenerationRequest request
    ) {
        Objects.requireNonNull(
                request,
                "request must not be null"
        );

        return generator.generate(request);
    }
}