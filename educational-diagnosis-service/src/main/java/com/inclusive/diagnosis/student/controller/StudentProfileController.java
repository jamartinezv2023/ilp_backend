package com.inclusive.diagnosis.student.controller;

import com.inclusive.diagnosis.student.dto.CreateStudentProfileRequest;
import com.inclusive.diagnosis.student.entity.StudentProfile;
import com.inclusive.diagnosis.student.repository.StudentProfileRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileRepository repository;

    @PostMapping
    public ResponseEntity<StudentProfile> create(
            @Valid @RequestBody CreateStudentProfileRequest request
    ) {

        StudentProfile student = StudentProfile.builder()
                .tenantId(request.tenantId())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .studentCode(request.studentCode())
                .birthDate(request.birthDate())
                .gradeLevel(request.gradeLevel())
                .institutionName(request.institutionName())
                .hasDisabilitySupportNeeds(
                        request.hasDisabilitySupportNeeds()
                )
                .accessibilityNotes(request.accessibilityNotes())
                .preferredLearningStyle(
                        request.preferredLearningStyle()
                )
                .communicationPreferences(
                        request.communicationPreferences()
                )
                .build();

        return ResponseEntity.ok(
                repository.save(student)
        );
    }

    @GetMapping
    public ResponseEntity<List<StudentProfile>> findAll() {
        return ResponseEntity.ok(
                repository.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentProfile> findById(
            @PathVariable UUID id
    ) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(
                        () -> ResponseEntity.notFound().build()
                );
    }
}
