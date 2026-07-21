package com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb;

import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class KolbResultMapper {

    public static final String ALGORITHM_VERSION =
            "KOLB_BASELINE_V1";

    public AssessmentResult toGenericResult(
            AssessmentSubmission submission,
            KolbAssessmentEngine.KolbScores scores
    ) {
        double abstractConcreteAxis =
                scores.scoreAC() - scores.scoreCE();

        double activeReflectiveAxis =
                scores.scoreAE() - scores.scoreRO();

        return new AssessmentResult(
                submission.administrationId(),
                submission.participantId(),
                submission.assessmentCode(),
                submission.assessmentVersion(),
                scores.learningStyle(),
                Map.of(
                        "CE", scores.scoreCE().doubleValue(),
                        "RO", scores.scoreRO().doubleValue(),
                        "AC", scores.scoreAC().doubleValue(),
                        "AE", scores.scoreAE().doubleValue(),
                        "AC_MINUS_CE", abstractConcreteAxis,
                        "AE_MINUS_RO", activeReflectiveAxis
                ),
                Map.of(
                        "LEARNING_STYLE",
                        scores.learningStyle()
                ),
                List.of(),
                ALGORITHM_VERSION,
                Instant.now()
        );
    }
}