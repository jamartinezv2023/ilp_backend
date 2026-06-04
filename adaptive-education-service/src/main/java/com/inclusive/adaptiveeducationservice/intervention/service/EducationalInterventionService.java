package com.inclusive.adaptiveeducationservice.intervention.service;

import com.inclusive.adaptiveeducationservice.intervention.dto.EducationalInterventionRequest;
import com.inclusive.adaptiveeducationservice.intervention.dto.EducationalInterventionResponse;
import com.inclusive.adaptiveeducationservice.intervention.entity.EducationalInterventionEntity;
import com.inclusive.adaptiveeducationservice.intervention.repository.EducationalInterventionRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class EducationalInterventionService {

    private final EducationalInterventionRepository interventionRepository;
    private final StudentProfileRepository studentProfileRepository;

    public EducationalInterventionService(
            EducationalInterventionRepository interventionRepository,
            StudentProfileRepository studentProfileRepository
    ) {
        this.interventionRepository = interventionRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    public EducationalInterventionResponse create(
            EducationalInterventionRequest request
    ) {
        if (!studentProfileRepository.existsById(request.studentId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Student profile not found"
            );
        }

        var now = Instant.now();
        var intervention = new EducationalInterventionEntity(
                "INT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                request.studentId(),
                request.title(),
                request.responsibleRole(),
                request.interventionType(),
                request.description(),
                "PLANNED",
                now,
                now
        );

        return toResponse(interventionRepository.save(intervention));
    }

    public List<EducationalInterventionResponse> findByStudentId(
            String studentId
    ) {
        return interventionRepository.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EducationalInterventionResponse updateStatus(
            String interventionId,
            String status
    ) {
        var intervention = interventionRepository.findById(interventionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Intervention not found"
                ));

        intervention.updateStatus(status);

        return toResponse(interventionRepository.save(intervention));
    }

    private EducationalInterventionResponse toResponse(
            EducationalInterventionEntity intervention
    ) {
        return new EducationalInterventionResponse(
                intervention.getId(),
                intervention.getStudentId(),
                intervention.getTitle(),
                intervention.getResponsibleRole(),
                intervention.getInterventionType(),
                intervention.getDescription(),
                intervention.getStatus(),
                intervention.getCreatedAt(),
                intervention.getUpdatedAt()
        );
    }
}