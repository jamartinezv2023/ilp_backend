package com.inclusive.adaptiveeducationservice.student.service;

import com.inclusive.adaptiveeducationservice.student.dto.StudentProfileRequest;
import com.inclusive.adaptiveeducationservice.student.dto.StudentProfileResponse;
import com.inclusive.adaptiveeducationservice.student.entity.StudentProfileEntity;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;

    public StudentProfileService(
            StudentProfileRepository studentProfileRepository
    ) {
        this.studentProfileRepository = studentProfileRepository;
    }

    public List<StudentProfileResponse> findAll() {
        return studentProfileRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<StudentProfileResponse> findById(String id) {
        return studentProfileRepository.findById(id)
                .map(this::toResponse);
    }

    public StudentProfileResponse create(
            String id,
            StudentProfileRequest request
    ) {
        var student = new StudentProfileEntity(
                id,
                request.fullName(),
                request.grade(),
                request.age(),
                request.learningProfile(),
                request.vocationalInterest(),
                request.supportLevel(),
                request.inclusiveStrategies(),
                request.pedagogicalRecommendations()
        );

        return toResponse(studentProfileRepository.save(student));
    }

    public Optional<StudentProfileResponse> update(
            String id,
            StudentProfileRequest request
    ) {
        return studentProfileRepository.findById(id)
                .map(student -> {
                    student.updateProfile(
                            request.fullName(),
                            request.grade(),
                            request.age(),
                            request.learningProfile(),
                            request.vocationalInterest(),
                            request.supportLevel(),
                            request.inclusiveStrategies(),
                            request.pedagogicalRecommendations()
                    );

                    return toResponse(studentProfileRepository.save(student));
                });
    }

    private StudentProfileResponse toResponse(StudentProfileEntity student) {
        return new StudentProfileResponse(
                student.getId(),
                student.getFullName(),
                student.getGrade(),
                student.getAge(),
                student.getLearningProfile(),
                student.getVocationalInterest(),
                student.getSupportLevel(),
                student.getInclusiveStrategies(),
                student.getPedagogicalRecommendations()
        );
    }
}