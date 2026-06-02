package com.inclusive.adaptiveeducationservice.student.service;

import com.inclusive.adaptiveeducationservice.student.entity.StudentProfileEntity;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StudentProfileServiceTest {

    private final StudentProfileRepository repository =
            mock(StudentProfileRepository.class);

    private final StudentProfileService service =
            new StudentProfileService(repository);

    @Test
    void shouldReturnAllStudents() {
        when(repository.findAll()).thenReturn(List.of(sampleStudent()));

        var students = service.findAll();

        assertThat(students).hasSize(1);
        assertThat(students.get(0).id()).isEqualTo("ST-001");
    }

    @Test
    void shouldReturnStudentById() {
        when(repository.findById("ST-001")).thenReturn(
                Optional.of(sampleStudent())
        );

        var student = service.findById("ST-001");

        assertThat(student).isPresent();
        assertThat(student.get().fullName()).contains("Mariana");
    }

    @Test
    void shouldReturnEmptyWhenStudentDoesNotExist() {
        when(repository.findById("ST-999")).thenReturn(Optional.empty());

        var student = service.findById("ST-999");

        assertThat(student).isEmpty();
    }

    private StudentProfileEntity sampleStudent() {
        return new StudentProfileEntity(
                "ST-001",
                "Mariana Gomez",
                "6A",
                11,
                "Divergent learning profile",
                "Arts and communication",
                "MEDIUM",
                List.of("Visual learning support"),
                List.of("Use visual organizers before complex tasks")
        );
    }
}