package com.inclusive.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.authservice.model.StudentEnvironment;

@Repository
public interface StudentEnvironmentRepository extends JpaRepository<StudentEnvironment, Long> {
}