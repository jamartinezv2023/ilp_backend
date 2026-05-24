package com.inclusive.diagnosis.analytics.controller;

import com.inclusive.diagnosis.analytics.dto.AdaptivePiarPlanResponse;
import com.inclusive.diagnosis.analytics.dto.AdaptiveSupportProfileResponse;
import com.inclusive.diagnosis.analytics.dto.EarlySupportAlertResponse;
import com.inclusive.diagnosis.analytics.dto.EducationalRiskProfileResponse;
import com.inclusive.diagnosis.analytics.dto.EvidenceTraceabilityResponse;
import com.inclusive.diagnosis.analytics.dto.InstitutionalGovernanceReportResponse;
import com.inclusive.diagnosis.analytics.dto.InstitutionalPolicyRecommendationResponse;
import com.inclusive.diagnosis.analytics.dto.LearningEvolutionResponse;
import com.inclusive.diagnosis.analytics.dto.LearningProfileSummaryResponse;
import com.inclusive.diagnosis.analytics.dto.PedagogicalPriorityPlanResponse;
import com.inclusive.diagnosis.analytics.dto.SupportEvolutionResponse;
import com.inclusive.diagnosis.analytics.dto.TeacherActionPlanResponse;
import com.inclusive.diagnosis.analytics.service.AdaptivePiarPlanService;
import com.inclusive.diagnosis.analytics.service.AdaptiveSupportProfileService;
import com.inclusive.diagnosis.analytics.service.EarlySupportAlertService;
import com.inclusive.diagnosis.analytics.service.EducationalRiskService;
import com.inclusive.diagnosis.analytics.service.EvidenceTraceabilityService;
import com.inclusive.diagnosis.analytics.service.InstitutionalGovernanceService;
import com.inclusive.diagnosis.analytics.service.InstitutionalPolicyRecommendationService;
import com.inclusive.diagnosis.analytics.service.LearningEvolutionService;
import com.inclusive.diagnosis.analytics.service.LearningProfileAggregationService;
import com.inclusive.diagnosis.analytics.service.PriorityRecommendationService;
import com.inclusive.diagnosis.analytics.service.SupportEvolutionService;
import com.inclusive.diagnosis.analytics.service.TeacherActionPlanService;
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

    private final LearningProfileAggregationService aggregationService;

    private final PriorityRecommendationService priorityRecommendationService;

    private final EvidenceTraceabilityService evidenceTraceabilityService;

    private final TeacherActionPlanService teacherActionPlanService;

    private final LearningEvolutionService learningEvolutionService;

    private final EducationalRiskService educationalRiskService;

    private final InstitutionalPolicyRecommendationService
            institutionalPolicyRecommendationService;

    private final InstitutionalGovernanceService
            institutionalGovernanceService;

    private final EarlySupportAlertService earlySupportAlertService;

    private final AdaptivePiarPlanService adaptivePiarPlanService;

    private final AdaptiveSupportProfileService adaptiveSupportProfileService;

    private final SupportEvolutionService supportEvolutionService;

    @GetMapping("/student/{studentId}/learning-profile")
    public ResponseEntity<LearningProfileSummaryResponse>
    summarizeLearningProfile(@PathVariable UUID studentId) {
        return ResponseEntity.ok(aggregationService.summarize(studentId));
    }

    @GetMapping("/student/{studentId}/priority-plan")
    public ResponseEntity<PedagogicalPriorityPlanResponse>
    generatePriorityPlan(@PathVariable UUID studentId) {
        return ResponseEntity.ok(
                priorityRecommendationService.generatePriorityPlan(studentId)
        );
    }

    @GetMapping("/student/{studentId}/evidence-trace")
    public ResponseEntity<EvidenceTraceabilityResponse>
    traceEvidence(@PathVariable UUID studentId) {
        return ResponseEntity.ok(evidenceTraceabilityService.trace(studentId));
    }

    @GetMapping("/student/{studentId}/action-plan")
    public ResponseEntity<TeacherActionPlanResponse>
    generateActionPlan(@PathVariable UUID studentId) {
        return ResponseEntity.ok(teacherActionPlanService.generate(studentId));
    }

    @GetMapping("/student/{studentId}/learning-evolution")
    public ResponseEntity<LearningEvolutionResponse>
    analyzeLearningEvolution(@PathVariable UUID studentId) {
        return ResponseEntity.ok(learningEvolutionService.analyze(studentId));
    }

    @GetMapping("/student/{studentId}/risk-profile")
    public ResponseEntity<EducationalRiskProfileResponse>
    analyzeRisk(@PathVariable UUID studentId) {
        return ResponseEntity.ok(educationalRiskService.analyzeRisk(studentId));
    }

    @GetMapping("/student/{studentId}/early-support-alert")
    public ResponseEntity<EarlySupportAlertResponse>
    analyzeEarlySupportAlert(@PathVariable UUID studentId) {
        return ResponseEntity.ok(earlySupportAlertService.analyze(studentId));
    }

    @GetMapping("/student/{studentId}/piar-plan")
    public ResponseEntity<AdaptivePiarPlanResponse>
    generatePiarPlan(@PathVariable UUID studentId) {
        return ResponseEntity.ok(adaptivePiarPlanService.generate(studentId));
    }

    @GetMapping("/student/{studentId}/support-profile")
    public ResponseEntity<AdaptiveSupportProfileResponse>
    generateSupportProfile(@PathVariable UUID studentId) {
        return ResponseEntity.ok(
                adaptiveSupportProfileService.generate(studentId)
        );
    }

    @GetMapping("/student/{studentId}/support-evolution")
    public ResponseEntity<SupportEvolutionResponse>
    analyzeSupportEvolution(@PathVariable UUID studentId) {
        return ResponseEntity.ok(supportEvolutionService.analyze(studentId));
    }

    @GetMapping("/institution/policy-recommendation")
    public ResponseEntity<InstitutionalPolicyRecommendationResponse>
    generateInstitutionalPolicyRecommendation() {
        return ResponseEntity.ok(
                institutionalPolicyRecommendationService.generate()
        );
    }

    @GetMapping("/institution/governance-report")
    public ResponseEntity<InstitutionalGovernanceReportResponse>
    generateGovernanceReport() {
        return ResponseEntity.ok(institutionalGovernanceService.generate());
    }
}
