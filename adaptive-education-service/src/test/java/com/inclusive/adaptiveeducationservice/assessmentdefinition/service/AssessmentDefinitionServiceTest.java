package com.inclusive.adaptiveeducationservice.assessmentdefinition.service;

import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentDefinitionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AssessmentDefinitionServiceTest {

    private final AssessmentDefinitionRepository repository =
            mock(AssessmentDefinitionRepository.class);

    private final AssessmentDefinitionService service =
            new AssessmentDefinitionService(repository);

    @Test
    void shouldReturnAllAssessmentDefinitions() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(
                new AssessmentDefinitionEntity(
                        "DEF-KOLB-V1",
                        "KOLB_V1",
                        "Kolb",
                        "Description",
                        "IPSATIVE_RANKING",
                        "1.0",
                        true,
                        20,
                        "Instructions",
                        Instant.now()
                )
        ));

        // Act
        var definitions = service.findAll();

        // Assert
        assertThat(definitions).hasSize(1);
        assertThat(definitions.get(0).code()).isEqualTo("KOLB_V1");
        assertThat(definitions.get(0).assessmentType())
                .isEqualTo("IPSATIVE_RANKING");
    }
}