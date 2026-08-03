package com.inclusive.adaptiveeducationservice.assessmentengine.generic.rendering;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata.AssessmentMetadataRegistry;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata.DefaultAssessmentMetadataProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentRendererModelFactoryTest {

    @Test
    void shouldBuildRendererModelInStableOrder() {
        AssessmentRendererModelFactory factory =
                new AssessmentRendererModelFactory(
                        new AssessmentMetadataRegistry(
                                List.of(
                                        new DefaultAssessmentMetadataProvider()
                                )
                        )
                );

        AssessmentRendererModel model =
                factory.create(definition());

        assertThat(model.code())
                .isEqualTo("TEST_V1");

        assertThat(model.questions())
                .extracting(
                        AssessmentRendererQuestion::code
                )
                .containsExactly("Q1", "Q2");

        assertThat(
                model.questions().get(0).options()
        ).extracting(
                AssessmentRendererOption::code
        ).containsExactly("A", "B");
    }

    private AssessmentDefinition definition() {
        AssessmentQuestion second =
                question("Q2", 2);

        AssessmentQuestion first =
                question("Q1", 1);

        return new AssessmentDefinition(
                "DEF-TEST",
                "TEST_V1",
                "Generic test",
                "Renderer test",
                "1.0",
                "TEST_SCORING",
                "TEST_INTERPRETATION",
                "Answer every question.",
                true,
                List.of(second, first),
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }

    private AssessmentQuestion question(
            String code,
            int order
    ) {
        return new AssessmentQuestion(
                "ID-" + code,
                code,
                "Question " + code,
                "GENERAL",
                AssessmentQuestionType.SINGLE_CHOICE,
                true,
                order,
                List.of(
                        option(code, "B", 2),
                        option(code, "A", 1)
                )
        );
    }

    private AssessmentOption option(
            String questionCode,
            String code,
            int order
    ) {
        return new AssessmentOption(
                questionCode + "-" + code,
                code,
                "Option " + code,
                "GENERAL",
                1.0,
                1.0,
                order
        );
    }
}