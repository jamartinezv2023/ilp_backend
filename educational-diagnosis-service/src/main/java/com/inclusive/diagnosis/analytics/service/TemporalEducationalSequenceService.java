package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.TemporalEducationalSequenceResponse;
import com.inclusive.diagnosis.analytics.dto.TemporalEducationalSignalResponse;
import com.inclusive.diagnosis.intervention.entity.InterventionExecution;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemporalEducationalSequenceService {

    private final InterventionExecutionRepository interventionRepository;

    public TemporalEducationalSequenceResponse generatePreview() {

        var interventions = interventionRepository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                InterventionExecution::getExecutedAt
                        )
                )
                .toList();

        String studentId = interventions.stream()
                .findFirst()
                .map(item -> item.getStudentProfileId().toString())
                .orElse("NO_TEMPORAL_SAMPLE");

        List<TemporalEducationalSignalResponse> timeline =
                interventions.stream()
                        .map(item -> {
                            double progressScore =
                                    item.getProgressScore();

                            double engagementRisk =
                                    Boolean.TRUE.equals(
                                            item.getEngagementImproved()
                                    )
                                            ? 0.20
                                            : 0.90;

                            double supportIntensity =
                                    progressScore < 0.5
                                            ? 0.90
                                            : progressScore < 0.8
                                                    ? 0.60
                                                    : 0.25;

                            return new TemporalEducationalSignalResponse(
                                    item.getExecutedAt().toString(),
                                    progressScore,
                                    engagementRisk,
                                    supportIntensity
                            );
                        })
                        .toList();

        String policy =
                "Temporal sequence excludes clinical labels and models only educational support evolution, engagement risk, intervention effectiveness and adaptive support intensity.";

        return new TemporalEducationalSequenceResponse(
                studentId,
                timeline,
                policy
        );
    }
}
