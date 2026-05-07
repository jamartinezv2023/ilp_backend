package com.inclusive.adaptiveeducationservice.infrastructure.persistence.adapter;

import com.inclusive.adaptiveeducationservice.application.ports.output.StudentCharacterizationPort;
import com.inclusive.adaptiveeducationservice.domain.model.StudentCharacterization;
import org.springframework.stereotype.Component;

@Component
public class StudentCharacterizationAdapter implements StudentCharacterizationPort {

    @Override
    public StudentCharacterization findCharacterizationByStudentId(Long studentId) {
        return new StudentCharacterization();
    }
}