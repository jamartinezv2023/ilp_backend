package com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import org.springframework.stereotype.Component;

@Component
public class KolbAssessmentMetadataProvider
        implements AssessmentMetadataProvider {

    private static final String KOLB_CODE = "KOLB_V1";

    @Override
    public boolean supports(String assessmentCode) {
        return KOLB_CODE.equals(assessmentCode);
    }

    @Override
    public AssessmentMetadata provide(
            AssessmentDefinition definition
    ) {
        return new AssessmentMetadata(
                definition.code(),
                definition.name(),
                "David A. Kolb",
                definition.version(),
                AssessmentInstrumentType.LEARNING_STYLE,
                "es",
                20,
                definition.description(),
                "Uso institucional y de investigación"
        );
    }

    @Override
    public int priority() {
        return 100;
    }
}