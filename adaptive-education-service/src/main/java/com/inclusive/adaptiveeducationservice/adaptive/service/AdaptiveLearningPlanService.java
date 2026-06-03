package com.inclusive.adaptiveeducationservice.adaptive.service;

import com.inclusive.adaptiveeducationservice.adaptive.dto.AdaptiveLearningPlanResponse;
import com.inclusive.adaptiveeducationservice.adaptive.entity.AdaptiveLearningPlanEntity;
import com.inclusive.adaptiveeducationservice.adaptive.repository.AdaptiveLearningPlanRepository;
import com.inclusive.adaptiveeducationservice.student.entity.StudentProfileEntity;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AdaptiveLearningPlanService {

    private final StudentProfileRepository studentProfileRepository;

    private final AdaptiveLearningEngine adaptiveLearningEngine;

    private final AdaptiveLearningPlanRepository adaptiveLearningPlanRepository;

    public AdaptiveLearningPlanService(
            StudentProfileRepository studentProfileRepository,
            AdaptiveLearningEngine adaptiveLearningEngine,
            AdaptiveLearningPlanRepository adaptiveLearningPlanRepository
    ) {
        this.studentProfileRepository = studentProfileRepository;
        this.adaptiveLearningEngine = adaptiveLearningEngine;
        this.adaptiveLearningPlanRepository = adaptiveLearningPlanRepository;
    }

    public AdaptiveLearningPlanResponse generateForStudent(String studentId) {
        var student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student profile not found"
                ));

        var plan = adaptiveLearningEngine.generate(student);
        var planId = "ALP-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        var entity = new AdaptiveLearningPlanEntity(
                planId,
                student.getId(),
                plan.riskLevel(),
                plan.recommendedMethodology(),
                plan.recommendedResources(),
                plan.adaptivePathway(),
                plan.teacherActions(),
                plan.inclusionActions(),
                plan.familyActions(),
                Instant.now()
        );

        return toResponse(
                adaptiveLearningPlanRepository.save(entity),
                student
        );
    }

    public List<AdaptiveLearningPlanResponse> historyByStudent(
            String studentId
    ) {
        var student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student profile not found"
                ));

        return adaptiveLearningPlanRepository
                .findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(plan -> toResponse(plan, student))
                .toList();
    }

    private AdaptiveLearningPlanResponse toResponse(
            AdaptiveLearningPlanEntity plan,
            StudentProfileEntity student
    ) {
        return new AdaptiveLearningPlanResponse(
                plan.getId(),
                student.getId(),
                student.getFullName(),
                student.getLearningProfile(),
                student.getLearningPreferences(),
                student.getVocationalInterest(),
                student.getSupportLevel(),
                plan.getRiskLevel(),
                plan.getRecommendedMethodology(),
                plan.getRecommendedResources(),
                plan.getAdaptivePathway(),
                plan.getTeacherActions(),
                plan.getInclusionActions(),
                plan.getFamilyActions(),
                plan.getCreatedAt()
        );
    }
}