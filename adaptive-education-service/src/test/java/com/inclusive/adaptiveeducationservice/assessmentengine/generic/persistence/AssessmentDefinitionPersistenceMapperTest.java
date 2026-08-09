package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence;

import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentDefinitionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentOptionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentQuestionEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssessmentDefinitionPersistenceMapperTest {

    private final AssessmentDefinitionPersistenceMapper mapper =
            new AssessmentDefinitionPersistenceMapper();

    @Test
    void shouldMapExistingEntityToGenericDomain() {
        AssessmentDefinitionEntity entity =
                existingKolbDefinition();

        AssessmentDefinition domain = mapper.toDomain(entity);

        assertEquals("KOLB_V1", domain.code());
        assertEquals("1.0", domain.version());
        assertTrue(domain.active());
        assertEquals(1, domain.questions().size());

        assertEquals(
                AssessmentQuestionType.IPSATIVE_RANKING,
                domain.questions().get(0).type()
        );

        assertEquals(
                "CE",
                domain.questions().get(0)
                        .options().get(0).code()
        );
    }

    @Test
    void shouldPreserveEssentialFieldsDuringRoundTrip() {
        AssessmentDefinition original =
                mapper.toDomain(existingKolbDefinition());

        AssessmentDefinition roundTrip =
                mapper.toDomain(mapper.toEntity(original));

        assertEquals(original.id(), roundTrip.id());
        assertEquals(original.code(), roundTrip.code());
        assertEquals(original.version(), roundTrip.version());

        assertEquals(
                original.questions().size(),
                roundTrip.questions().size()
        );

        assertEquals(
                original.questions().get(0).options().size(),
                roundTrip.questions().get(0).options().size()
        );
    }

    @Test
    void shouldRejectNullPersistedQuestionType() {
        AssessmentDefinitionEntity entity =
                definitionWithQuestionType(null);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> mapper.toDomain(entity)
                );

        assertEquals(
                "Persisted question type is required",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectBlankPersistedQuestionType() {
        AssessmentDefinitionEntity entity =
                definitionWithQuestionType("   ");

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> mapper.toDomain(entity)
                );

        assertEquals(
                "Persisted question type is required",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectUnsupportedPersistedQuestionType() {
        AssessmentDefinitionEntity entity =
                definitionWithQuestionType("UNSUPPORTED");

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> mapper.toDomain(entity)
                );

        assertEquals(
                "Unsupported persisted question type: UNSUPPORTED",
                exception.getMessage()
        );
    }

    private AssessmentDefinitionEntity existingKolbDefinition() {
        return definitionWithQuestionType(
                "IPSATIVE_RANKING"
        );
    }

    private AssessmentDefinitionEntity definitionWithQuestionType(
            String questionType
    ) {
        AssessmentDefinitionEntity definition =
                new AssessmentDefinitionEntity(
                        "DEF-KOLB-V1",
                        "KOLB_V1",
                        "Kolb Learning Style Inventory",
                        "Kolb test",
                        "IPSATIVE_RANKING",
                        "1.0",
                        true,
                        20,
                        "Rank each statement.",
                        Instant.parse("2026-01-01T00:00:00Z")
                );

        AssessmentQuestionEntity question =
                new AssessmentQuestionEntity(
                        "KOLB-V1-Q001",
                        1,
                        "Rank the statements.",
                        "CE_RO_AC_AE",
                        "Use every rank once.",
                        true,
                        questionType,
                        1
                );

        question.addOption(
                new AssessmentOptionEntity(
                        "KOLB-V1-Q001-CE",
                        "Concrete experience",
                        "CE",
                        0,
                        1
                )
        );

        definition.addQuestion(question);

        return definition;
    }
}
