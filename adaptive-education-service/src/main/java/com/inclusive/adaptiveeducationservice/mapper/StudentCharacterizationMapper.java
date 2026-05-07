package com.inclusive.adaptiveeducationservice.mapper;

import com.inclusive.adaptiveeducationservice.application.dto.StudentCharacterizationResponse;
import com.inclusive.adaptiveeducationservice.domain.model.StudentCharacterization;
import org.springframework.stereotype.Component;

@Component
public class StudentCharacterizationMapper {

    public StudentCharacterizationResponse toResponse(
            StudentCharacterization characterization
    ) {
        return new StudentCharacterizationResponse(
                null,
                null,
                null
        );
    }
}