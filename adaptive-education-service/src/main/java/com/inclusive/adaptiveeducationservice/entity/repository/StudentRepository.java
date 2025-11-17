package com.inclusive.adaptiveeducationservice.entity.repository;

import com.inclusive.adaptiveeducationservice.entity.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    Optional<Student> findByEmail(String email);
}
