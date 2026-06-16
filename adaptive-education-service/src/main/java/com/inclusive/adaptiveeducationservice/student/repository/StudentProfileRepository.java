package com.inclusive.adaptiveeducationservice.student.repository;

import com.inclusive.adaptiveeducationservice.student.entity.StudentProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProfileRepository
        extends JpaRepository<StudentProfileEntity, String> {
}