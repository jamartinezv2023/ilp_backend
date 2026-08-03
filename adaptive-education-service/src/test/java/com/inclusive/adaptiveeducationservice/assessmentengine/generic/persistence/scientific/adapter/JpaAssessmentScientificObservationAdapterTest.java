package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.adapter;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.PersistAssessmentScientificObservationCommand;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentScientificResultEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentSubmissionContextEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentScientificResultRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentSubmissionContextRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaAssessmentScientificObservationAdapterTest {

    private AssessmentScientificResultRepository
            resultRepository;

    private AssessmentSubmissionContextRepository
            contextRepository;

    private AssessmentScientificObservationMapper mapper;

    private JpaAssessmentScientificObservationAdapter adapter;

    @BeforeEach
    void setUp() {
        resultRepository =
                mock(
                        AssessmentScientificResultRepository.class
                );

        contextRepository =
                mock(
                        AssessmentSubmissionContextRepository.class
                );

        mapper =
                mock(
                        AssessmentScientificObservationMapper.class
                );

        adapter =
                new JpaAssessmentScientificObservationAdapter(
                        resultRepository,
                        contextRepository,
                        mapper
                );
    }

    @Test
    void shouldPersistResultAndContext() {
        PersistAssessmentScientificObservationCommand command =
                command();

        AssessmentScientificResultEntity resultEntity =
                mock(
                        AssessmentScientificResultEntity.class
                );

        AssessmentSubmissionContextEntity contextEntity =
                mock(
                        AssessmentSubmissionContextEntity.class
                );

        when(
                resultRepository.existsByAdministrationId(
                        "ADMIN-001"
                )
        ).thenReturn(false);

        when(
                contextRepository.existsByAdministrationId(
                        "ADMIN-001"
                )
        ).thenReturn(false);

        when(
                mapper.toResultEntity(command)
        ).thenReturn(resultEntity);

        when(
                mapper.toContextEntity(command)
        ).thenReturn(contextEntity);

        adapter.save(command);

        verify(mapper)
                .toResultEntity(command);

        verify(mapper)
                .toContextEntity(command);

        verify(resultRepository)
                .save(resultEntity);

        verify(contextRepository)
                .save(contextEntity);
    }

    @Test
    void shouldRejectDuplicatedScientificObservation() {
        PersistAssessmentScientificObservationCommand command =
                command();

        when(
                resultRepository.existsByAdministrationId(
                        "ADMIN-001"
                )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                adapter.save(command)
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessageContaining(
                        "Scientific observation already exists"
                );

        verify(mapper, never())
                .toResultEntity(command);

        verify(mapper, never())
                .toContextEntity(command);

        verify(resultRepository, never())
                .save(
                        org.mockito.ArgumentMatchers.any()
                );

        verify(contextRepository, never())
                .save(
                        org.mockito.ArgumentMatchers.any()
                );
    }

    @Test
    void shouldDetectContextOnlyPartialObservation() {
        when(
                resultRepository.existsByAdministrationId(
                        "ADMIN-001"
                )
        ).thenReturn(false);

        when(
                contextRepository.existsByAdministrationId(
                        "ADMIN-001"
                )
        ).thenReturn(true);

        assertThat(
                adapter.existsByAdministrationId(
                        "ADMIN-001"
                )
        ).isTrue();
    }

    private PersistAssessmentScientificObservationCommand
    command() {
        Instant submittedAt =
                Instant.parse(
                        "2026-07-22T10:00:00Z"
                );

        AssessmentSubmission submission =
                new AssessmentSubmission(
                        "ADMIN-001",
                        "ST-001",
                        "KOLB_V1",
                        "1.0",
                        List.of(),
                        Map.of(),
                        submittedAt
                );

        AssessmentResult result =
                new AssessmentResult(
                        "ADMIN-001",
                        "ST-001",
                        "KOLB_V1",
                        "1.0",
                        "DIVERGENT",
                        Map.of(
                                "CE",
                                48.0
                        ),
                        Map.of(
                                "PRIMARY_PROFILE",
                                "Perfil divergente"
                        ),
                        List.of(),
                        "KOLB_BASELINE_V1",
                        submittedAt.plusSeconds(1)
                );

        return new PersistAssessmentScientificObservationCommand(
                submission,
                result
        );
    }
}