package com.inclusive.common.mapper.student;

import com.inclusive.common.domain.student.Student;
import com.inclusive.common.dto.student.StudentIdentityDTO;
import com.inclusive.common.dto.student.StudentProfileDTO;

public final class StudentMapper {

    private StudentMapper() {
    }

    public static StudentIdentityDTO toIdentityDTO(Student student) {
        if (student == null) {
            return null;
        }

        return new StudentIdentityDTO(
                student.getId(),
                student.getFullName(),
                student.isActive()
        );
    }

    public static StudentProfileDTO toProfileDTO(Student student) {
        if (student == null) {
            return null;
        }

        StudentProfileDTO dto = new StudentProfileDTO();
        dto.setId(student.getId());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setGender(student.getGender());
        dto.setLocation(student.getLocation());
        dto.setActive(student.isActive());

        return dto;
    }

    public static Student toEntity(StudentProfileDTO dto) {
        if (dto == null) {
            return null;
        }

        return Student.create(
                dto.getFullName(),
                dto.getEmail(),
                dto.getGender(),
                dto.getLocation()
        );
    }
}