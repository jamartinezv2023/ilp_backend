package com.inclusive.adaptiveeducationservice.entity.service;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    List<StudentDTO> findAll();

    Optional<StudentDTO> findById(Long id);

    StudentDTO create(StudentDTO dto);

    Optional<StudentDTO> update(Long id, StudentDTO dto);

    void delete(Long id);
}
