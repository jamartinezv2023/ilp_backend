package com.inclusive.adaptiveeducationservice.assessmentresponse.service;

import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import org.springframework.web.server.ResponseStatusException;

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
    void rejectsRawWriteBeforeReadingOrSaving() {
        assertThatThrownBy(() -> service.submit(null))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> org.assertj.core.api.Assertions.assertThat(
                        ((ResponseStatusException) error).getStatusCode().value()
                ).isEqualTo(410));
        verifyNoInteractions(responseRepository, studentRepository, definitionRepository);
    }
}
