package com.inclusive.adaptiveeducationservice.recommendation.service;

import com.inclusive.adaptiveeducationservice.student.entity.StudentProfileEntity;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EducationalRecommendationServiceTest {

    private final StudentProfileRepository repository =
            mock(StudentProfileRepository.class);

    private final EducationalRecommendationService service =
            new EducationalRecommendationService(repository);

    @Test
    void shouldGenerateRecommendationsForStudent() {
        var student = new StudentProfileEntity(
                "ST-001",
                "Mariana Gomez",
                "6A",
                11,
                "DIVERGENT",
                "SCIENTIFIC",
                "HIGH",
                List.of("Visual learning support"),
                List.of("Use visual organizers")
        );

        student.updateLearningPreferences(List.of("VISUAL", "ACTIVE"));

        when(repository.findById("ST-001")).thenReturn(Optional.of(student));

        var response = service.generateForStudent("ST-001");

        assertThat(response.studentId()).isEqualTo("ST-001");
        assertThat(response.teacherRecommendations()).isNotEmpty();
        assertThat(response.inclusionRecommendations()).isNotEmpty();
        assertThat(response.familyRecommendations()).isNotEmpty();
        assertThat(response.nextActions()).contains("Schedule inclusion team review.");
    }
}