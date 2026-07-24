package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.query;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentScientificResultEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentSubmissionContextEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentScientificResultRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentSubmissionContextRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaAssessmentScientificObservationQueryAdapterTest {

    private AssessmentScientificResultRepository resultRepository;
    private AssessmentSubmissionContextRepository contextRepository;
    private AssessmentScientificObservationQueryMapper mapper;

    private JpaAssessmentScientificObservationQueryAdapter adapter;

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
                        AssessmentScientificObservationQueryMapper.class
                );

        adapter =
                new JpaAssessmentScientificObservationQueryAdapter(
                        resultRepository,
                        contextRepository,
                        mapper
                );
    }

    @Test
    void shouldReturnCompleteObservation() {
        AssessmentScientificResultEntity result =
                result();

        AssessmentSubmissionContextEntity context =
                context();

        AssessmentScientificObservation observation =
                mock(
                        AssessmentScientificObservation.class
                );

        when(
                resultRepository.findByAdministrationId(
                        "ADMIN-001"
                )
        ).thenReturn(
                Optional.of(result)
        );

        when(
                contextRepository.findByAdministrationId(
                        "ADMIN-001"
                )
        ).thenReturn(
                Optional.of(context)
        );

        when(
                mapper.toModel(
                        result,
                        context
                )
        ).thenReturn(observation);

        assertThat(
                adapter.findByAdministrationId(
                        "ADMIN-001"
                )
        )
                .containsSame(observation);

        verify(mapper)
                .toModel(
                        result,
                        context
                );
    }

    @Test
    void shouldReturnEmptyWhenResultDoesNotExist() {
        when(
                resultRepository.findByAdministrationId(
                        "ADMIN-404"
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThat(
                adapter.findByAdministrationId(
                        "ADMIN-404"
                )
        ).isEmpty();

        verify(
                contextRepository,
                never()
        ).findByAdministrationId(
                "ADMIN-404"
        );
    }

    @Test
    void shouldReturnEmptyWhenContextDoesNotExist() {
        AssessmentScientificResultEntity result =
                result();

        when(
                resultRepository.findByAdministrationId(
                        "ADMIN-001"
                )
        ).thenReturn(
                Optional.of(result)
        );

        when(
                contextRepository.findByAdministrationId(
                        "ADMIN-001"
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThat(
                adapter.findByAdministrationId(
                        "ADMIN-001"
                )
        ).isEmpty();

        verify(
                mapper,
                never()
        ).toModel(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    private AssessmentScientificResultEntity result() {
        Instant timestamp =
                Instant.parse(
                        "2026-07-24T10:00:00Z"
                );

        return new AssessmentScientificResultEntity(
                "RESULT-ADMIN-001",
                "ADMIN-001",
                "ST-001",
                "KOLB_V1",
                "1.0",
                "DIVERGENT",
                "KOLB_BASELINE_V1",
                "KOLB_INTERPRETATION_V1",
                timestamp,
                timestamp,
                timestamp
        );
    }

    private AssessmentSubmissionContextEntity context() {
        Instant timestamp =
                Instant.parse(
                        "2026-07-24T10:00:00Z"
                );

        return new AssessmentSubmissionContextEntity(
                "CONTEXT-ADMIN-001",
                "ADMIN-001",
                timestamp
        );
    }
}