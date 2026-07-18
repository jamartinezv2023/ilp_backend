package com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import org.springframework.stereotype.Component;

@Component
public class DefaultAssessmentMetadataProvider
        implements AssessmentMetadataProvider {

    @Override
    public boolean supports(String assessmentCode) {
        return assessmentCode != null
                && !assessmentCode.isBlank();
    }

    @Override
    public AssessmentMetadata provide(
            AssessmentDefinition definition
    ) {
        return new AssessmentMetadata(
                definition.code(),
                definition.name(),
                "ILP",
                definition.version(),
                AssessmentInstrumentType.CUSTOM,
                "es",
                20,
                definition.description(),
                "Uso institucional y de investigación"
        );
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }
}