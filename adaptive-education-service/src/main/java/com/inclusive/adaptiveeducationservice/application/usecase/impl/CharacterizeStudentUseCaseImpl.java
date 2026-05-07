package com.inclusive.adaptiveeducationservice.application.usecase.impl;

import com.inclusive.adaptiveeducationservice.application.ports.input.CharacterizeStudentUseCase;
import com.inclusive.adaptiveeducationservice.application.ports.output.StudentCharacterizationPort;
import com.inclusive.adaptiveeducationservice.domain.model.StudentCharacterization;
import org.springframework.stereotype.Service;

@Service
public class CharacterizeStudentUseCaseImpl implements CharacterizeStudentUseCase {

    private final StudentCharacterizationPort studentCharacterizationPort;

    public CharacterizeStudentUseCaseImpl(StudentCharacterizationPort studentCharacterizationPort) {
        this.studentCharacterizationPort = studentCharacterizationPort;
    }

    @Override
    public StudentCharacterization characterizeStudent(Long studentId) {
        return studentCharacterizationPort.findCharacterizationByStudentId(studentId);
    }
}