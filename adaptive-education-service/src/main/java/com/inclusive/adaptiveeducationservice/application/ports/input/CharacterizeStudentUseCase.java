package com.inclusive.adaptiveeducationservice.application.ports.input;

import com.inclusive.adaptiveeducationservice.domain.model.StudentCharacterization;

public interface CharacterizeStudentUseCase {

    StudentCharacterization characterizeStudent(Long studentId);
}
