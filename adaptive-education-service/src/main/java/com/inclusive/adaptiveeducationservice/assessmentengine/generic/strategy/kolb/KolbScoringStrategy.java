package com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb;

import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.AssessmentScoringStrategy;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class KolbScoringStrategy
        implements AssessmentScoringStrategy {

    private static final Set<String> SUPPORTED_CODES =
            Set.of(
                    "KOLB_V1",
                    "KOLB_BASELINE_V1"
            );

    private final KolbAssessmentEngine kolbAssessmentEngine;
    private final KolbSubmissionMapper submissionMapper;
    private final KolbResultMapper resultMapper;

    public KolbScoringStrategy(
            KolbAssessmentEngine kolbAssessmentEngine,
            KolbSubmissionMapper submissionMapper,
            KolbResultMapper resultMapper
    ) {
        this.kolbAssessmentEngine = kolbAssessmentEngine;
        this.submissionMapper = submissionMapper;
        this.resultMapper = resultMapper;
    }

    @Override
    public boolean supports(String assessmentCode) {
        return assessmentCode != null
                && SUPPORTED_CODES.contains(assessmentCode);
    }

    @Override
    public AssessmentResult score(
            AssessmentDefinition definition,
            AssessmentSubmission submission
    ) {
        var answers = submissionMapper.toAnswers(
                definition,
                submission
        );

        var kolbScores = kolbAssessmentEngine.calculate(answers);

        return resultMapper.toGenericResult(
                submission,
                kolbScores
        );
    }
}