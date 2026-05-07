package com.inclusive.adaptiveeducationservice.application.usecase.impl;

import com.inclusive.adaptiveeducationservice.application.ports.output.StudentCharacterizationPort;
import com.inclusive.adaptiveeducationservice.domain.model.StudentCharacterization;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class CharacterizeStudentUseCaseImplTest {

    @Test
    void shouldReturnStudentCharacterizationWhenStudentExists() {
        // Arrange
        Long studentId = 1L;
        StudentCharacterization expectedCharacterization = new StudentCharacterization();

        StudentCharacterizationPort studentCharacterizationPort =
                id -> expectedCharacterization;

        CharacterizeStudentUseCaseImpl useCase =
                new CharacterizeStudentUseCaseImpl(studentCharacterizationPort);

        // Act
        StudentCharacterization actualCharacterization =
                useCase.characterizeStudent(studentId);

        // Assert
        assertSame(expectedCharacterization, actualCharacterization);
    }
}