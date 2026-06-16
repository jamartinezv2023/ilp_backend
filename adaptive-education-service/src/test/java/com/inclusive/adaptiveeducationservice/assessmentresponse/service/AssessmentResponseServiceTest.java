package com.inclusive.adaptiveeducationservice.assessmentresponse.service;

import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentAnswerRequest;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentResponseRequest;
import com.inclusive.adaptiveeducationservice.assessmentresponse.entity.AssessmentResponseEntity;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AssessmentResponseServiceTest {

    private final AssessmentResponseRepository responseRepository =
            mock(AssessmentResponseRepository.class);

    private final StudentProfileRepository studentRepository =
            mock(StudentProfileRepository.class);

    private final AssessmentDefinitionRepository definitionRepository =
            mock(AssessmentDefinitionRepository.class);

    private final AssessmentResponseService service =
            new AssessmentResponseService(
                    responseRepository,
                    studentRepository,
                    definitionRepository
            );

    @Test
    void shouldPersistAssessmentResponseWithAnswers() {
        // Arrange
        when(studentRepository.existsById("ST-001")).thenReturn(true);
        when(definitionRepository.existsByCode("KOLB_V1")).thenReturn(true);
        when(responseRepository.save(any(AssessmentResponseEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var request = new AssessmentResponseRequest(
                "ST-001",
                "KOLB_V1",
                "1.0",
                List.of(
                        new AssessmentAnswerRequest(
                                "KOLB-V1-Q001",
                                "KOLB-V1-Q001-CE",
                                "CE",
                                "CE",
                                4
                        )
                )
        );

        // Act
        var response = service.submit(request);

        // Assert
        assertThat(response.studentId()).isEqualTo("ST-001");
        assertThat(response.assessmentCode()).isEqualTo("KOLB_V1");
        assertThat(response.status()).isEqualTo("SUBMITTED");
        assertThat(response.answers()).hasSize(1);
        assertThat(response.answers().get(0).score()).isEqualTo(4);
    }
}