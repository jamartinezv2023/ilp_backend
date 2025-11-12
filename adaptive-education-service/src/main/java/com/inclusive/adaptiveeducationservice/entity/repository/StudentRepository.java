package com.inclusive.adaptiveeducationservice.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.adaptiveeducationservice.entity.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}