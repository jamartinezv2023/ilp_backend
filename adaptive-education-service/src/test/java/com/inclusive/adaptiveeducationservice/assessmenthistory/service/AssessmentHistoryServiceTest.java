package com.inclusive.adaptiveeducationservice.assessmenthistory.service;

import com.inclusive.adaptiveeducationservice.assessmentresponse.entity.AssessmentAnswerEntity;
import com.inclusive.adaptiveeducationservice.assessmentresponse.entity.AssessmentResponseEntity;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AssessmentHistoryServiceTest {

    private final AssessmentResponseRepository responseRepository =
            mock(AssessmentResponseRepository.class);

    private final StudentProfileRepository studentRepository =
            mock(StudentProfileRepository.class);

    private final AssessmentHistoryService service =
            new AssessmentHistoryService(responseRepository, studentRepository);

    @Test
    void shouldReturnAssessmentHistoryForStudent() {
        // Arrange
        var response = new AssessmentResponseEntity(
                "AR-001",
                "ST-001",
                "KOLB_V1",
                "1.0",
                "SUBMITTED",
                Instant.now()
        );

        response.addAnswer(new AssessmentAnswerEntity(
                "AR-001-A001",
                "KOLB-V1-Q001",
                "KOLB-V1-Q001-CE",
                "CE",
                "CE",
                4
        ));

        when(studentRepository.existsById("ST-001")).thenReturn(true);
        when(responseRepository.findByStudentIdOrderBySubmittedAtDesc("ST-001"))
                .thenReturn(List.of(response));

        // Act
        var history = service.findStudentAssessmentHistory("ST-001");

        // Assert
        assertThat(history).hasSize(1);
        assertThat(history.get(0).assessmentCode()).isEqualTo("KOLB_V1");
        assertThat(history.get(0).answerCount()).isEqualTo(1);
        assertThat(history.get(0).totalScore()).isEqualTo(4);
    }

    @Test
    void shouldReturnTimelineForStudent() {
        // Arrange
        var response = new AssessmentResponseEntity(
                "AR-001",
                "ST-001",
                "KOLB_V1",
                "1.0",
                "SUBMITTED",
                Instant.now()
        );

        when(studentRepository.existsById("ST-001")).thenReturn(true);
        when(responseRepository.findByStudentIdOrderBySubmittedAtDesc("ST-001"))
                .thenReturn(List.of(response));

        // Act
        var timeline = service.findStudentTimeline("ST-001");

        // Assert
        assertThat(timeline).hasSize(1);
        assertThat(timeline.get(0).eventType())
                .isEqualTo("ASSESSMENT_SUBMITTED");
    }
}