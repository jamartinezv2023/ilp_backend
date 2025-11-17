package com.inclusive.adaptiveeducationservice.entity.service.impl;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;
import com.inclusive.adaptiveeducationservice.entity.model.Student;
import com.inclusive.adaptiveeducationservice.entity.model.StudentMapper;
import com.inclusive.adaptiveeducationservice.entity.repository.StudentRepository;
import com.inclusive.adaptiveeducationservice.entity.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;

    public StudentServiceImpl(StudentRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentDTO> findById(Long id) {
        return repository.findById(id).map(StudentMapper::toDto);
    }

    @Override
    public StudentDTO create(StudentDTO dto) {
        if (dto.getEmail() != null && repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }
        Student entity = StudentMapper.toEntity(dto);
        if (entity.getAccountStatus() == null) {
            entity.setAccountStatus("ACTIVE");
        }
        OffsetDateTime now = OffsetDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        Student saved = repository.save(entity);
        return StudentMapper.toDto(saved);
    }

    @Override
    public Optional<StudentDTO> update(Long id, StudentDTO dto) {
        return repository.findById(id).map(existing -> {
            existing.setFullName(dto.getFullName());
            existing.setEmail(dto.getEmail());
            existing.setAge(dto.getAge());
            existing.setGender(dto.getGender());
            existing.setDisabilityStatus(dto.getDisabilityStatus());
            existing.setSchoolLevel(dto.getSchoolLevel());
            existing.setAttendanceRate(dto.getAttendanceRate());
            existing.setAverageGrade(dto.getAverageGrade());
            existing.setMathScore(dto.getMathScore());
            existing.setReadingScore(dto.getReadingScore());
            existing.setScienceScore(dto.getScienceScore());
            existing.setDeviceAccess(dto.getDeviceAccess());
            existing.setInternetAccess(dto.getInternetAccess());
            existing.setPreferredStudyTime(dto.getPreferredStudyTime());
            existing.setUpdatedAt(OffsetDateTime.now());
            Student saved = repository.save(existing);
            return StudentMapper.toDto(saved);
        });
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
