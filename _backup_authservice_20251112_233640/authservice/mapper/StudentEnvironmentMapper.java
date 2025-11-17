package com.inclusive.authservice.mapper;

import org.springframework.stereotype.Component;
import com.inclusive.authservice.dto.StudentEnvironmentDTO;
import com.inclusive.authservice.model.StudentEnvironment;

@Component
public class StudentEnvironmentMapper {
    public StudentEnvironment toEntity(StudentEnvironmentDTO dto) {
        return null; // Deprecated mapper, use ObjectMapper instead
    }

    public StudentEnvironmentDTO toDto(StudentEnvironment entity) {
        return null; // Deprecated mapper, use ObjectMapper instead
    }
}





