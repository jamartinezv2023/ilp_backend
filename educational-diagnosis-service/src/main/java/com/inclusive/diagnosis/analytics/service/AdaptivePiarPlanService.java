package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.AdaptivePiarPlanResponse;
import com.inclusive.diagnosis.diagnosis.repository.InclusiveDiagnosisRepository;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import com.inclusive.diagnosis.student.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdaptivePiarPlanService {

    private final StudentProfileRepository studentRepository;

    private final InclusiveDiagnosisRepository diagnosisRepository;

    private final InterventionExecutionRepository interventionRepository;

    public AdaptivePiarPlanResponse generate(
            UUID studentProfileId
    ) {

        var student = studentRepository.findById(studentProfileId)
                .orElse(null);

        var diagnosis = diagnosisRepository
                .findByStudentProfileId(studentProfileId)
                .stream()
                .findFirst()
                .orElse(null);

        var interventions = interventionRepository
                .findByStudentProfileId(studentProfileId);

        boolean hasSupportNeeds = student != null
                && Boolean.TRUE.equals(
                        student.getHasDisabilitySupportNeeds()
                );

        boolean hasBarrier = diagnosis != null
                && diagnosis.getIdentifiedBarriers() != null
                && !diagnosis.getIdentifiedBarriers().isBlank();

        boolean lowProgress = interventions.stream()
                .anyMatch(item -> item.getProgressScore() < 0.6);

        String supportNeedArea = hasSupportNeeds || hasBarrier || lowProgress
                ? "INCLUSIVE_PEDAGOGICAL_SUPPORT"
                : "UNIVERSAL_CLASSROOM_SUPPORT";

        String detectedBarrier = hasBarrier
                ? diagnosis.getIdentifiedBarriers()
                : "No specific barrier documented yet.";

        String reasonableAdjustment = lowProgress || hasBarrier
                ? "Provide flexible pacing, alternative evidence of learning, structured routines and explicit written instructions."
                : "Continue universal supports and collect additional classroom evidence.";

        String duaStrategy = "Multiple means of engagement, representation, action and expression.";

        String evaluationAdaptation = lowProgress || hasBarrier
                ? "Allow asynchronous reflective assessment, oral/visual alternatives, guided tasks and extended time."
                : "Use standard assessment with optional accessibility supports.";

        String followUp = lowProgress || hasBarrier
                ? "Schedule weekly monitoring, document classroom evidence and review support plan with the institutional team."
                : "Continue observation and update support plan if new barriers appear.";

        List<String> supportCategories = List.of(
                "Attention and executive functioning support",
                "Communication and language support",
                "Sensory and accessibility support",
                "Socio-emotional support",
                "Learning pace and evaluation flexibility",
                "Family and institutional support coordination"
        );

        String ethicalWarning =
                "This PIAR-oriented plan does not establish a medical diagnosis. It organizes reasonable educational adjustments based on pedagogical evidence and observed support needs.";

        return new AdaptivePiarPlanResponse(
                studentProfileId.toString(),
                supportNeedArea,
                detectedBarrier,
                reasonableAdjustment,
                duaStrategy,
                evaluationAdaptation,
                followUp,
                supportCategories,
                ethicalWarning
        );
    }
}
