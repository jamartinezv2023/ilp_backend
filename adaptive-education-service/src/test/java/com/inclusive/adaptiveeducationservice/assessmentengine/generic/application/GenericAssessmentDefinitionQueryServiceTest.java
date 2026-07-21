package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.AssessmentDefinitionNotFoundException;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata.AssessmentMetadataRegistry;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata.DefaultAssessmentMetadataProvider;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.AssessmentDefinitionRepositoryPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.rendering.AssessmentRendererModelFactory;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenericAssessmentDefinitionQueryServiceTest {

    private final AssessmentDefinitionRepositoryPort repository =
            mock(AssessmentDefinitionRepositoryPort.class);

    private final AssessmentRendererModelFactory factory =
            new AssessmentRendererModelFactory(
                    new AssessmentMetadataRegistry(
                            List.of(
                                    new DefaultAssessmentMetadataProvider()
                            )
                    )
            );

    private final GenericAssessmentDefinitionQueryService service =
            new GenericAssessmentDefinitionQueryService(
                    repository,
                    factory
            );

    @Test
    void shouldReturnRendererModelForActiveDefinition() {
        when(
                repository.findLatestActiveByCode("TEST_V1")
        ).thenReturn(Optional.of(definition()));

        var model = service.findRendererModel("TEST_V1");

        assertThat(model.code())
                .isEqualTo("TEST_V1");

        assertThat(model.version())
                .isEqualTo("1.0");

        assertThat(model.metadata().language())
                .isEqualTo("es");
    }

    @Test
    void shouldReturnAllActiveRendererModels() {
        when(repository.findAllActive())
                .thenReturn(List.of(definition()));

        var models =
                service.findAllActiveRendererModels();

        assertThat(models)
                .hasSize(1);

        assertThat(models.get(0).code())
                .isEqualTo("TEST_V1");
    }

    @Test
    void shouldRejectUnknownAssessment() {
        when(
                repository.findLatestActiveByCode("UNKNOWN")
        ).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.findRendererModel("UNKNOWN")
        ).isInstanceOf(
                AssessmentDefinitionNotFoundException.class
        );
    }

    private AssessmentDefinition definition() {
        return new AssessmentDefinition(
                "DEF-TEST",
                "TEST_V1",
                "Generic assessment",
                "Generic renderer assessment",
                "1.0",
                "TEST_SCORING",
                "TEST_INTERPRETATION",
                "Complete the instrument.",
                true,
                List.of(),
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }
}