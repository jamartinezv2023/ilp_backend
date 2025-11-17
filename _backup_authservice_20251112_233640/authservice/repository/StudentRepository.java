package com.inclusive.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.authservice.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}