package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.AdaptiveSupportProfileResponse;
import com.inclusive.diagnosis.analytics.dto.SupportDimensionResponse;
import com.inclusive.diagnosis.diagnosis.repository.InclusiveDiagnosisRepository;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdaptiveSupportProfileService {

    private final InclusiveDiagnosisRepository diagnosisRepository;

    private final InterventionExecutionRepository interventionRepository;

    public AdaptiveSupportProfileResponse generate(
            UUID studentProfileId
    ) {

        var diagnoses = diagnosisRepository.findByStudentProfileId(
                studentProfileId
        );

        var interventions = interventionRepository.findByStudentProfileId(
                studentProfileId
        );

        List<SupportDimensionResponse> dimensions = new ArrayList<>();

        boolean hasInteractionBarrier = diagnoses.stream()
                .anyMatch(item -> item.getIdentifiedBarriers() != null
                        && item.getIdentifiedBarriers()
                        .toLowerCase()
                        .contains("interaction"));

        boolean lowProgress = interventions.stream()
                .anyMatch(item -> item.getProgressScore() < 0.6);

        boolean noEngagementImprovement = !interventions.isEmpty()
                && interventions.stream()
                .noneMatch(item -> Boolean.TRUE.equals(
                        item.getEngagementImproved()
                ));

        if (lowProgress || noEngagementImprovement) {
            dimensions.add(
                    new SupportDimensionResponse(
                            "ATTENTION_EXECUTIVE_SUPPORT",
                            lowProgress && noEngagementImprovement
                                    ? 0.78
                                    : 0.62,
                            List.of(
                                    "Low intervention progress observed",
                                    "Sustained engagement difficulty observed"
                            )
                    )
            );
        }

        if (hasInteractionBarrier) {
            dimensions.add(
                    new SupportDimensionResponse(
                            "SOCIAL_INTERACTION_SUPPORT",
                            0.74,
                            List.of(
                                    "Interaction barrier detected in inclusive diagnosis",
                                    "Participation support may be required"
                            )
                    )
            );
        }

        if (lowProgress) {
            dimensions.add(
                    new SupportDimensionResponse(
                            "ADAPTIVE_PACING_SUPPORT",
                            0.72,
                            List.of(
                                    "Progress score below expected threshold",
                                    "Flexible pacing may be required"
                            )
                    )
            );
        }

        if (dimensions.isEmpty()) {
            dimensions.add(
                    new SupportDimensionResponse(
                            "UNIVERSAL_CLASSROOM_SUPPORT",
                            0.40,
                            List.of(
                                    "No specific support dimension detected yet",
                                    "Continue collecting pedagogical evidence"
                            )
                    )
            );
        }

        String warning =
                "This support profile does not diagnose disabilities or disorders. It identifies observed educational support dimensions based on pedagogical evidence.";

        return new AdaptiveSupportProfileResponse(
                studentProfileId.toString(),
                dimensions,
                warning
        );
    }
}
