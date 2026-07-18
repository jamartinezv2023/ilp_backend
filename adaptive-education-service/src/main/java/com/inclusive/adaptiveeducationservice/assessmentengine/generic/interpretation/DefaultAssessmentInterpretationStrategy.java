package com.inclusive.adaptiveeducationservice.assessmentengine.generic.interpretation;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DefaultAssessmentInterpretationStrategy
        implements AssessmentInterpretationStrategy {

    @Override
    public boolean supports(String assessmentCode) {
        return assessmentCode != null
                && !assessmentCode.isBlank();
    }

    @Override
    public AssessmentInterpretation interpret(
            AssessmentDefinition definition,
            AssessmentResult result
    ) {
        String profile = result.primaryProfile();

        String narrative =
                "Resultado del instrumento "
                        + definition.name()
                        + ": "
                        + profile
                        + ".";

        return new AssessmentInterpretation(
                profile,
                null,
                narrative,
                Map.of(
                        "assessmentCode",
                        definition.code(),
                        "assessmentVersion",
                        definition.version(),
                        "algorithmVersion",
                        result.scoringAlgorithmVersion()
                ),
                List.of(
                        "Analizar el resultado junto con "
                                + "otras evidencias educativas.",
                        "Evitar decisiones pedagógicas basadas "
                                + "en un único instrumento."
                )
        );
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }
}