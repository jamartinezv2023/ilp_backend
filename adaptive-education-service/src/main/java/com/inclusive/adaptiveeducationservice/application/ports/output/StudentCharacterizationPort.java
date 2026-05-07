package com.inclusive.adaptiveeducationservice.application.ports.output;

import com.inclusive.adaptiveeducationservice.domain.model.StudentCharacterization;

public interface StudentCharacterizationPort {

    StudentCharacterization findCharacterizationByStudentId(Long studentId);
}
