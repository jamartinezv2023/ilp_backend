package com.inclusive.diagnosis.student.repository;

import com.inclusive.diagnosis.student.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentProfileRepository
        extends JpaRepository<StudentProfile, UUID> {
}
