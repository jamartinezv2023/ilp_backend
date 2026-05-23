package com.inclusive.diagnosis.analytics.controller;

import com.inclusive.diagnosis.analytics.dto.EvidenceTraceabilityResponse;
import com.inclusive.diagnosis.analytics.dto.LearningProfileSummaryResponse;
import com.inclusive.diagnosis.analytics.dto.PedagogicalPriorityPlanResponse;
import com.inclusive.diagnosis.analytics.service.EvidenceTraceabilityService;
import com.inclusive.diagnosis.analytics.service.LearningProfileAggregationService;
import com.inclusive.diagnosis.analytics.service.PriorityRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class LearningAnalyticsController {

    private final LearningProfileAggregationService
            aggregationService;

    private final PriorityRecommendationService
            priorityRecommendationService;

    private final EvidenceTraceabilityService
            evidenceTraceabilityService;

    @GetMapping("/student/{studentId}/learning-profile")
    public ResponseEntity<LearningProfileSummaryResponse>
    summarizeLearningProfile(
            @PathVariable UUID studentId
    ) {

        return ResponseEntity.ok(
                aggregationService.summarize(studentId)
        );
    }

    @GetMapping("/student/{studentId}/priority-plan")
    public ResponseEntity<PedagogicalPriorityPlanResponse>
    generatePriorityPlan(
            @PathVariable UUID studentId
    ) {

        return ResponseEntity.ok(
                priorityRecommendationService.generatePriorityPlan(
                        studentId
                )
        );
    }

    @GetMapping("/student/{studentId}/evidence-trace")
    public ResponseEntity<EvidenceTraceabilityResponse>
    traceEvidence(
            @PathVariable UUID studentId
    ) {

        return ResponseEntity.ok(
                evidenceTraceabilityService.trace(studentId)
        );
    }
}
