package com.inclusive.authservice.mapper;

import com.inclusive.authservice.dto.student.StudentRequestDTO;
import com.inclusive.authservice.dto.student.StudentResponseDTO;
import com.inclusive.authservice.dto.student.StudentEnvironmentDTO;
import com.inclusive.authservice.dto.student.StudentSupportNeedsDTO;
import com.inclusive.authservice.model.Student;
import com.inclusive.authservice.model.StudentEnvironment;
import com.inclusive.authservice.model.StudentSupportNeeds;
import org.mapstruct.*;

/**
 * Mapper interface for converting between Student entities and DTOs.
 * Uses MapStruct to generate implementation automatically.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    // ===== Student mapping =====
    Student toEntity(StudentRequestDTO dto);

    StudentResponseDTO toResponseDTO(Student entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(StudentRequestDTO dto, @MappingTarget Student entity);

    // ===== Environment mapping =====
    StudentEnvironment toEnvironmentEntity(StudentEnvironmentDTO dto);
    StudentEnvironmentDTO toEnvironmentDTO(StudentEnvironment entity);

    // ===== Support Needs mapping =====
    StudentSupportNeeds toSupportNeedsEntity(StudentSupportNeedsDTO dto);
    StudentSupportNeedsDTO toSupportNeedsDTO(StudentSupportNeeds entity);
}







