package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence;

import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentDefinitionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AssessmentDefinitionJpaAdapterTest {

    private final AssessmentDefinitionRepository repository =
            mock(AssessmentDefinitionRepository.class);

    private final AssessmentDefinitionPersistenceMapper mapper =
            new AssessmentDefinitionPersistenceMapper();

    private final AssessmentDefinitionJpaAdapter adapter =
            new AssessmentDefinitionJpaAdapter(repository, mapper);

    @Test
    void shouldFindExactCodeAndVersion() {
        AssessmentDefinitionEntity entity = entity();

        when(repository.findByCode("KOLB_V1"))
                .thenReturn(Optional.of(entity));

        Optional<AssessmentDefinition> result =
                adapter.findByCodeAndVersion(
                        "KOLB_V1",
                        "1.0"
                );

        assertTrue(result.isPresent());
        assertEquals("KOLB_V1", result.get().code());
    }

    @Test
    void shouldRejectDifferentVersion() {
        when(repository.findByCode("KOLB_V1"))
                .thenReturn(Optional.of(entity()));

        assertFalse(
                adapter.existsByCodeAndVersion(
                        "KOLB_V1",
                        "2.0"
                )
        );
    }

    @Test
    void shouldListActiveDefinitions() {
        when(repository.findAllByActiveTrueOrderByCodeAsc())
                .thenReturn(List.of(entity()));

        List<AssessmentDefinition> result =
                adapter.findAllActive();

        assertEquals(1, result.size());
        assertEquals("KOLB_V1", result.get(0).code());
    }

    private AssessmentDefinitionEntity entity() {
        return new AssessmentDefinitionEntity(
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
    }
}