package com.inclusive.diagnosis.intervention.controller;

import com.inclusive.diagnosis.intervention.dto.CreateInterventionExecutionRequest;
import com.inclusive.diagnosis.intervention.entity.InterventionExecution;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/interventions")
@RequiredArgsConstructor
public class InterventionExecutionController {

    private final InterventionExecutionRepository repository;

    @PostMapping
    public ResponseEntity<InterventionExecution>
    create(
            @Valid
            @RequestBody
            CreateInterventionExecutionRequest request
    ) {

        InterventionExecution execution =
                new InterventionExecution();

        execution.setId(UUID.randomUUID());
        execution.setTenantId(request.tenantId());
        execution.setStudentProfileId(
                request.studentProfileId()
        );
        execution.setInterventionCategory(
                request.interventionCategory()
        );
        execution.setInterventionDescription(
                request.interventionDescription()
        );
        execution.setResponsibleTeacher(
                request.responsibleTeacher()
        );
        execution.setEngagementImproved(
                request.engagementImproved()
        );
        execution.setProgressScore(
                request.progressScore()
        );
        execution.setTeacherObservations(
                request.teacherObservations()
        );
        execution.setExecutedAt(LocalDateTime.now());

        return ResponseEntity.ok(
                repository.save(execution)
        );
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<InterventionExecution>>
    findByStudent(
            @PathVariable UUID studentId
    ) {

        return ResponseEntity.ok(
                repository.findByStudentProfileId(studentId)
        );
    }
}
