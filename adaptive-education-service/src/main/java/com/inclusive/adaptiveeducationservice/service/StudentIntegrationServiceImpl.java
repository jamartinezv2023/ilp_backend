package com.inclusive.adaptiveeducationservice.service;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;
import com.inclusive.adaptiveeducationservice.entity.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentIntegrationServiceImpl implements StudentIntegrationService {

    private final StudentService studentService;

    public StudentIntegrationServiceImpl(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public List<StudentDTO> getAll() {
        return studentService.findAll();
    }

    @Override
    public Optional<StudentDTO> getById(Long id) {
        return studentService.findById(id);
    }

    @Override
    public StudentDTO create(StudentDTO dto) {
        return studentService.create(dto);
    }

    @Override
    public Optional<StudentDTO> update(Long id, StudentDTO dto) {
        return studentService.update(id, dto);
    }

    @Override
    public void delete(Long id) {
        studentService.delete(id);
    }
}
