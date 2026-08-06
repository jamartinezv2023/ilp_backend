package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.provider;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;

import java.util.List;

/**
 * Output port that supplies scientific feature items for a feature generation
 * request.
 *
 * <p>Implementations may obtain or calculate features from a specific
 * scientific, psychopedagogical, academic, or research source. This port
 * remains independent from persistence, transport, framework, and
 * infrastructure concerns.</p>
 *
 * <p>The generated feature vector remains the responsibility of the
 * scientific feature generator. A provider supplies only the feature items
 * required by that generation process.</p>
 */
@FunctionalInterface
public interface ScientificFeatureProvider {

    /**
     * Provides scientific feature items associated with the supplied
     * generation request.
     *
     * @param request scientific feature generation request
     * @return scientific feature items supplied by this provider
     */
    List<ScientificFeatureItem> provide(
            ScientificFeatureGenerationRequest request
    );
}
