package com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class AssessmentMetadataRegistry {

    private final List<AssessmentMetadataProvider> providers;

    public AssessmentMetadataRegistry(
            List<AssessmentMetadataProvider> providers
    ) {
        this.providers = providers.stream()
                .sorted(
                        Comparator.comparingInt(
                                AssessmentMetadataProvider::priority
                        ).reversed()
                )
                .toList();
    }

    public AssessmentMetadata metadataFor(
            AssessmentDefinition definition
    ) {
        return providers.stream()
                .filter(
                        provider ->
                                provider.supports(
                                        definition.code()
                                )
                )
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(
                                "No assessment metadata provider "
                                        + "supports: "
                                        + definition.code()
                        )
                )
                .provide(definition);
    }
}