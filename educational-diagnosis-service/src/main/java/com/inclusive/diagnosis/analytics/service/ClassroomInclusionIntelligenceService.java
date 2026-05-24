package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.ClassroomInclusionProfileResponse;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import com.inclusive.diagnosis.student.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomInclusionIntelligenceService {

    private final StudentProfileRepository studentRepository;

    private final InterventionExecutionRepository interventionRepository;

    public ClassroomInclusionProfileResponse analyze() {

        var students = studentRepository.findAll();

        var interventions = interventionRepository.findAll();

        int studentsAnalyzed = students.size();

        int adaptiveSupportCount = (int) interventions.stream()
                .filter(item -> item.getProgressScore() < 0.7)
                .map(item -> item.getStudentProfileId())
                .distinct()
                .count();

        int piarCount = (int) interventions.stream()
                .filter(item -> item.getProgressScore() < 0.5)
                .map(item -> item.getStudentProfileId())
                .distinct()
                .count();

        String complexity;

        if (piarCount >= 10 || adaptiveSupportCount >= 20) {
            complexity = "CRITICAL";
        } else if (piarCount >= 5 || adaptiveSupportCount >= 10) {
            complexity = "HIGH";
        } else if (piarCount >= 2 || adaptiveSupportCount >= 5) {
            complexity = "MODERATE";
        } else {
            complexity = "LOW";
        }

        String strategy;

        if ("CRITICAL".equals(complexity)) {
            strategy =
                    "Implement co-teaching, structured inclusive routines, multimodal instruction and institutional support escalation.";
        } else if ("HIGH".equals(complexity)) {
            strategy =
                    "Use structured routines, multimodal instruction and flexible pacing strategies.";
        } else if ("MODERATE".equals(complexity)) {
            strategy =
                    "Maintain differentiated instruction and periodic support monitoring.";
        } else {
            strategy =
                    "Continue universal classroom inclusive strategies.";
        }

        return new ClassroomInclusionProfileResponse(
                complexity,
                studentsAnalyzed,
                adaptiveSupportCount,
                piarCount,
                "ATTENTION_EXECUTIVE_SUPPORT",
                strategy,
                List.of(
                        "Executive functioning support",
                        "Flexible pacing support",
                        "Participation and engagement support",
                        "Multimodal instructional support"
                )
        );
    }
}
