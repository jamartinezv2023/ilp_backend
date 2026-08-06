package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.AssessmentDefinitionNotFoundException;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.DuplicateAssessmentDefinitionException;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.AssessmentDefinitionRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
class AssessmentDefinitionServiceTest {

    @Test
    void shouldRegisterDefinition() {
        InMemoryRepository repository = new InMemoryRepository();

        AssessmentDefinitionService service =
                new AssessmentDefinitionService(
                        repository,
                        new AssessmentDefinitionValidator()
                );

        AssessmentDefinition saved =
                service.register(definition("KOLB_V1", "1.0", true));

        assertEquals("KOLB_V1", saved.code());
        assertEquals("1.0", saved.version());
        assertEquals(1, repository.definitions.size());
    }

    @Test
    void shouldRejectDuplicatedCodeAndVersion() {
        InMemoryRepository repository = new InMemoryRepository();

        AssessmentDefinitionService service =
                new AssessmentDefinitionService(
                        repository,
                        new AssessmentDefinitionValidator()
                );

        service.register(definition("KOLB_V1", "1.0", true));

        assertThrows(
                DuplicateAssessmentDefinitionException.class,
                () -> service.register(
                        definition("KOLB_V1", "1.0", true)
                )
        );
    }

    @Test
    void shouldFindExactVersion() {
        InMemoryRepository repository = new InMemoryRepository();

        AssessmentDefinitionService service =
                new AssessmentDefinitionService(
                        repository,
                        new AssessmentDefinitionValidator()
                );

        service.register(definition("KOLB_V1", "1.0", true));
        service.register(definition("KOLB_V1", "2.0", true));

        AssessmentDefinition found =
                service.find("KOLB_V1", "1.0");

        assertEquals("1.0", found.version());
    }

    @Test
    void shouldFindLatestActiveVersion() {
        InMemoryRepository repository = new InMemoryRepository();

        AssessmentDefinitionService service =
                new AssessmentDefinitionService(
                        repository,
                        new AssessmentDefinitionValidator()
                );

        service.register(definition("KOLB_V1", "1.0", true));
        service.register(definition("KOLB_V1", "2.0", true));
        service.register(definition("KOLB_V1", "3.0", false));

        AssessmentDefinition latest =
                service.findLatestActive("KOLB_V1");

        assertEquals("2.0", latest.version());
    }

    @Test
    void shouldListVersions() {
        InMemoryRepository repository = new InMemoryRepository();

        AssessmentDefinitionService service =
                new AssessmentDefinitionService(
                        repository,
                        new AssessmentDefinitionValidator()
                );

        service.register(definition("KOLB_V1", "1.0", true));
        service.register(definition("KOLB_V1", "2.0", true));

        List<AssessmentDefinition> versions =
                service.findVersions("KOLB_V1");

        assertEquals(2, versions.size());
    }

    @Test
    void shouldRejectUnknownDefinition() {
        AssessmentDefinitionService service =
                new AssessmentDefinitionService(
                        new InMemoryRepository(),
                        new AssessmentDefinitionValidator()
                );

        assertThrows(
                AssessmentDefinitionNotFoundException.class,
                () -> service.find("UNKNOWN", "1.0")
        );
    }

    @Test
    void shouldRejectActiveDefinitionWithoutQuestions() {
        AssessmentDefinition invalid =
                new AssessmentDefinition(
                        "DEF-EMPTY",
                        "EMPTY_V1",
                        "Empty assessment",
                        "Invalid active definition",
                        "1.0",
                        "EMPTY_STRATEGY",
                        "EMPTY_INTERPRETATION",
                        "Instructions",
                        true,
                        List.of(),
                        Instant.now(),
                        Instant.now()
                );

        AssessmentDefinitionService service =
                new AssessmentDefinitionService(
                        new InMemoryRepository(),
                        new AssessmentDefinitionValidator()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(invalid)
        );
    }

    @Test
    void shouldReturnOnlyActiveDefinitions() {
        InMemoryRepository repository = new InMemoryRepository();

        AssessmentDefinitionService service =
                new AssessmentDefinitionService(
                        repository,
                        new AssessmentDefinitionValidator()
                );

        service.register(definition("KOLB_V1", "1.0", true));
        service.register(definition("FELDER_V1", "1.0", false));

        List<AssessmentDefinition> active =
                service.findAllActive();

        assertEquals(1, active.size());
        assertEquals("KOLB_V1", active.get(0).code());
    }

    private AssessmentDefinition definition(
            String code,
            String version,
            boolean active
    ) {
        AssessmentOption option =
                new AssessmentOption(
                        code + "-OPTION-1",
                        "OPTION_1",
                        "Option one",
                        "TEST",
                        1.0,
                        1.0,
                        1
                );

        AssessmentQuestion question =
                new AssessmentQuestion(
                        code + "-QUESTION-1",
                        "Q1",
                        "Question one",
                        "TEST",
                        AssessmentQuestionType.SINGLE_CHOICE,
                        true,
                        1,
                        List.of(option)
                );

        return new AssessmentDefinition(
                code + "-" + version,
                code,
                code + " assessment",
                "Test definition",
                version,
                code + "_SCORING",
                code + "_INTERPRETATION",
                "Answer every question",
                active,
                List.of(question),
                Instant.now(),
                Instant.now()
        );
    }

    private static class InMemoryRepository
            implements AssessmentDefinitionRepositoryPort {

        private final List<AssessmentDefinition> definitions =
                new ArrayList<>();

        @Override
        public AssessmentDefinition save(
                AssessmentDefinition definition
        ) {
            definitions.add(definition);
            return definition;
        }

        @Override
        public boolean existsByCodeAndVersion(
                String assessmentCode,
                String version
        ) {
            return definitions.stream()
                    .anyMatch(
                            definition ->
                                    definition.code()
                                            .equals(assessmentCode)
                                    && definition.version()
                                            .equals(version)
                    );
        }

        @Override
        public Optional<AssessmentDefinition> findByCodeAndVersion(
                String assessmentCode,
                String version
        ) {
            return definitions.stream()
                    .filter(
                            definition ->
                                    definition.code()
                                            .equals(assessmentCode)
                                    && definition.version()
                                            .equals(version)
                    )
                    .findFirst();
        }

        @Override
        public Optional<AssessmentDefinition> findLatestActiveByCode(
                String assessmentCode
        ) {
            return definitions.stream()
                    .filter(AssessmentDefinition::active)
                    .filter(
                            definition ->
                                    definition.code()
                                            .equals(assessmentCode)
                    )
                    .max(
                            Comparator.comparing(
                                    AssessmentDefinition::version
                            )
                    );
        }

        @Override
        public List<AssessmentDefinition> findAllByCode(
                String assessmentCode
        ) {
            return definitions.stream()
                    .filter(
                            definition ->
                                    definition.code()
                                            .equals(assessmentCode)
                    )
                    .toList();
        }

        @Override
        public List<AssessmentDefinition> findAllActive() {
            return definitions.stream()
                    .filter(AssessmentDefinition::active)
                    .toList();
        }
    }
}