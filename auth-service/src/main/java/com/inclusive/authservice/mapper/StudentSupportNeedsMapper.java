package com.inclusive.authservice.mapper;

import org.springframework.stereotype.Component;
import com.inclusive.authservice.dto.StudentSupportNeedsDTO;
import com.inclusive.authservice.model.StudentSupportNeeds;

@Component
public class StudentSupportNeedsMapper {
    public StudentSupportNeeds toEntity(StudentSupportNeedsDTO dto) {
        return null; // Deprecated mapper, use ObjectMapper instead
    }

    public StudentSupportNeedsDTO toDto(StudentSupportNeeds entity) {
        return null; // Deprecated mapper, use ObjectMapper instead
    }
}




