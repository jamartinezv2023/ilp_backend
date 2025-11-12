package com.inclusive.adaptiveeducationservice.repository;

import com.inclusive.adaptiveeducationservice.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}



