package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.interpretation.AssessmentInterpretationRegistry;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.interpretation.DefaultAssessmentInterpretationStrategy;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata.AssessmentMetadataRegistry;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata.DefaultAssessmentMetadataProvider;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.AssessmentSubmissionValidator;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.AssessmentScoringStrategy;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.AssessmentStrategyRegistry;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class InstrumentIndependentAssessmentServiceTest {

    @Test
    void shouldEvaluateWithoutKnowingInstrumentImplementation() {
        AssessmentScoringStrategy strategy =
                new AssessmentScoringStrategy() {

                    @Override
                    public boolean supports(
                            String assessmentCode
                    ) {
                        return "TEST_V1".equals(
                                assessmentCode
                        );
                    }

                    @Override
                    public AssessmentResult score(
                            AssessmentDefinition definition,
                            AssessmentSubmission submission
                    ) {
                        return new AssessmentResult(
                                submission.administrationId(),
                                submission.participantId(),
                                submission.assessmentCode(),
                                submission.assessmentVersion(),
                                "GENERIC_PROFILE",
                                Map.of("TOTAL", 10.0),
                                Map.of(),
                                List.of(),
                                "TEST_ALGORITHM_V1",
                                Instant.parse(
                                        "2026-01-01T00:00:00Z"
                                )
                        );
                    }
                };

        GenericAssessmentEngine engine =
                new GenericAssessmentEngine(
                        new AssessmentStrategyRegistry(
                                List.of(strategy)
                        ),
                        new AssessmentSubmissionValidator()
                );

        InstrumentIndependentAssessmentService service =
                new InstrumentIndependentAssessmentService(
                        engine,
                        new AssessmentMetadataRegistry(
                                List.of(
                                        new DefaultAssessmentMetadataProvider()
                                )
                        ),
                        new AssessmentInterpretationRegistry(
                                List.of(
                                        new DefaultAssessmentInterpretationStrategy()
                                )
                        )
                );

        InterpretedAssessmentResult result =
                service.evaluate(
                        definition(),
                        submission()
                );

        assertThat(result.rawResult().primaryProfile())
                .isEqualTo("GENERIC_PROFILE");

        assertThat(result.metadata().code())
                .isEqualTo("TEST_V1");

        assertThat(
                result.interpretation().primaryProfile()
        ).isEqualTo("GENERIC_PROFILE");
    }

    private AssessmentDefinition definition() {
        return new AssessmentDefinition(
                "DEF-TEST",
                "TEST_V1",
                "Generic test",
                "Instrument-independent test",
                "1.0",
                "TEST_SCORING",
                "TEST_INTERPRETATION",
                "Instructions",
                false,
                List.of(),
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }

    private AssessmentSubmission submission() {
        return new AssessmentSubmission(
                "ADMIN-1",
                "ST-1",
                "TEST_V1",
                "1.0",
                List.<AssessmentResponse>of(),
                Map.of("source", "unit-test"),
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }
}