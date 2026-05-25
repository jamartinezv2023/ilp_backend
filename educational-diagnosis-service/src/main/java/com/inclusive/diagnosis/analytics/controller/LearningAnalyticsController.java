package com.inclusive.diagnosis.analytics.controller;

import com.inclusive.diagnosis.analytics.dto.AdaptivePiarPlanResponse;
import com.inclusive.diagnosis.analytics.dto.AdaptiveRecommendationPreviewResponse;
import com.inclusive.diagnosis.analytics.dto.AdaptiveSupportProfileResponse;
import com.inclusive.diagnosis.analytics.dto.ClassroomInclusionProfileResponse;
import com.inclusive.diagnosis.analytics.dto.EarlySupportAlertResponse;
import com.inclusive.diagnosis.analytics.dto.EducationalFeatureStorePreviewResponse;
import com.inclusive.diagnosis.analytics.dto.EducationalMlDatasetPreviewResponse;
import com.inclusive.diagnosis.analytics.dto.EducationalRiskProfileResponse;
import com.inclusive.diagnosis.analytics.dto.EvidenceTraceabilityResponse;
import com.inclusive.diagnosis.analytics.dto.InstitutionalGovernanceReportResponse;
import com.inclusive.diagnosis.analytics.dto.InstitutionalPolicyRecommendationResponse;
import com.inclusive.diagnosis.analytics.dto.InterventionOrchestrationPreviewResponse;
import com.inclusive.diagnosis.analytics.dto.LearningEvolutionResponse;
import com.inclusive.diagnosis.analytics.dto.LearningProfileSummaryResponse;
import com.inclusive.diagnosis.analytics.dto.PedagogicalPriorityPlanResponse;
import com.inclusive.diagnosis.analytics.dto.PolicyLearningPreviewResponse;
import com.inclusive.diagnosis.analytics.dto.PsychopedagogicalProfileResponse;
import com.inclusive.diagnosis.analytics.dto.ReinforcementSignalPreviewResponse;
import com.inclusive.diagnosis.analytics.dto.SupportEvolutionResponse;
import com.inclusive.diagnosis.analytics.dto.SupportForecastPreviewResponse;
import com.inclusive.diagnosis.analytics.dto.TeacherActionPlanResponse;
import com.inclusive.diagnosis.analytics.dto.TemporalEducationalSequenceResponse;
import com.inclusive.diagnosis.analytics.service.AdaptivePiarPlanService;
import com.inclusive.diagnosis.analytics.service.AdaptiveRecommendationPreviewService;
import com.inclusive.diagnosis.analytics.service.AdaptiveSupportProfileService;
import com.inclusive.diagnosis.analytics.service.ClassroomInclusionIntelligenceService;
import com.inclusive.diagnosis.analytics.service.EarlySupportAlertService;
import com.inclusive.diagnosis.analytics.service.EducationalFeatureStorePreviewService;
import com.inclusive.diagnosis.analytics.service.EducationalMlDatasetPreviewService;
import com.inclusive.diagnosis.analytics.service.EducationalRiskService;
import com.inclusive.diagnosis.analytics.service.EvidenceTraceabilityService;
import com.inclusive.diagnosis.analytics.service.InstitutionalGovernanceService;
import com.inclusive.diagnosis.analytics.service.InstitutionalPolicyRecommendationService;
import com.inclusive.diagnosis.analytics.service.InterventionOrchestrationPreviewService;
import com.inclusive.diagnosis.analytics.service.LearningEvolutionService;
import com.inclusive.diagnosis.analytics.service.LearningProfileAggregationService;
import com.inclusive.diagnosis.analytics.service.PolicyLearningPreviewService;
import com.inclusive.diagnosis.analytics.service.PriorityRecommendationService;
import com.inclusive.diagnosis.analytics.service.PsychopedagogicalProfileService;
import com.inclusive.diagnosis.analytics.service.ReinforcementSignalPreviewService;
import com.inclusive.diagnosis.analytics.service.SupportEvolutionService;
import com.inclusive.diagnosis.analytics.service.SupportForecastPreviewService;
import com.inclusive.diagnosis.analytics.service.TeacherActionPlanService;
import com.inclusive.diagnosis.analytics.service.TemporalEducationalSequenceService;
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
    private final InstitutionalPolicyRecommendationService institutionalPolicyRecommendationService;
    private final InstitutionalGovernanceService institutionalGovernanceService;
    private final EarlySupportAlertService earlySupportAlertService;
    private final AdaptivePiarPlanService adaptivePiarPlanService;
    private final AdaptiveSupportProfileService adaptiveSupportProfileService;
    private final SupportEvolutionService supportEvolutionService;
    private final ClassroomInclusionIntelligenceService classroomInclusionIntelligenceService;
    private final PsychopedagogicalProfileService psychopedagogicalProfileService;
    private final EducationalMlDatasetPreviewService educationalMlDatasetPreviewService;
    private final EducationalFeatureStorePreviewService educationalFeatureStorePreviewService;
    private final TemporalEducationalSequenceService temporalEducationalSequenceService;
    private final SupportForecastPreviewService supportForecastPreviewService;
    private final AdaptiveRecommendationPreviewService adaptiveRecommendationPreviewService;
    private final InterventionOrchestrationPreviewService interventionOrchestrationPreviewService;
    private final PolicyLearningPreviewService policyLearningPreviewService;
    private final ReinforcementSignalPreviewService reinforcementSignalPreviewService;

    @GetMapping("/student/{studentId}/learning-profile")
    public ResponseEntity<LearningProfileSummaryResponse>
    summarizeLearningProfile(@PathVariable UUID studentId) {
        return ResponseEntity.ok(aggregationService.summarize(studentId));
    }

    @GetMapping("/student/{studentId}/priority-plan")
    public ResponseEntity<PedagogicalPriorityPlanResponse>
    generatePriorityPlan(@PathVariable UUID studentId) {
        return ResponseEntity.ok(priorityRecommendationService.generatePriorityPlan(studentId));
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
        return ResponseEntity.ok(adaptiveSupportProfileService.generate(studentId));
    }

    @GetMapping("/student/{studentId}/support-evolution")
    public ResponseEntity<SupportEvolutionResponse>
    analyzeSupportEvolution(@PathVariable UUID studentId) {
        return ResponseEntity.ok(supportEvolutionService.analyze(studentId));
    }

    @GetMapping("/student/{studentId}/psychopedagogical-profile")
    public ResponseEntity<PsychopedagogicalProfileResponse>
    generatePsychopedagogicalProfile(@PathVariable UUID studentId) {
        return ResponseEntity.ok(psychopedagogicalProfileService.generate(studentId));
    }

    @GetMapping("/classroom/inclusion-profile")
    public ResponseEntity<ClassroomInclusionProfileResponse>
    analyzeClassroomInclusion() {
        return ResponseEntity.ok(classroomInclusionIntelligenceService.analyze());
    }

    @GetMapping("/institution/policy-recommendation")
    public ResponseEntity<InstitutionalPolicyRecommendationResponse>
    generateInstitutionalPolicyRecommendation() {
        return ResponseEntity.ok(institutionalPolicyRecommendationService.generate());
    }

    @GetMapping("/institution/governance-report")
    public ResponseEntity<InstitutionalGovernanceReportResponse>
    generateGovernanceReport() {
        return ResponseEntity.ok(institutionalGovernanceService.generate());
    }

    @GetMapping("/ml/dataset-preview")
    public ResponseEntity<EducationalMlDatasetPreviewResponse>
    generateMlDatasetPreview() {
        return ResponseEntity.ok(educationalMlDatasetPreviewService.generatePreview());
    }

    @GetMapping("/ml/feature-store-preview")
    public ResponseEntity<EducationalFeatureStorePreviewResponse>
    generateFeatureStorePreview() {
        return ResponseEntity.ok(educationalFeatureStorePreviewService.generatePreview());
    }

    @GetMapping("/ml/temporal-sequence-preview")
    public ResponseEntity<TemporalEducationalSequenceResponse>
    generateTemporalSequencePreview() {
        return ResponseEntity.ok(temporalEducationalSequenceService.generatePreview());
    }

    @GetMapping("/ml/support-forecast-preview")
    public ResponseEntity<SupportForecastPreviewResponse>
    generateSupportForecastPreview() {
        return ResponseEntity.ok(supportForecastPreviewService.generatePreview());
    }

    @GetMapping("/ml/adaptive-recommendation-preview")
    public ResponseEntity<AdaptiveRecommendationPreviewResponse>
    generateAdaptiveRecommendationPreview() {
        return ResponseEntity.ok(adaptiveRecommendationPreviewService.generatePreview());
    }

    @GetMapping("/ml/intervention-orchestration-preview")
    public ResponseEntity<InterventionOrchestrationPreviewResponse>
    generateInterventionOrchestrationPreview() {
        return ResponseEntity.ok(interventionOrchestrationPreviewService.generatePreview());
    }

    @GetMapping("/ml/policy-learning-preview")
    public ResponseEntity<PolicyLearningPreviewResponse>
    generatePolicyLearningPreview() {
        return ResponseEntity.ok(policyLearningPreviewService.generatePreview());
    }

    @GetMapping("/ml/reinforcement-signal-preview")
    public ResponseEntity<ReinforcementSignalPreviewResponse>
    generateReinforcementSignalPreview() {
        return ResponseEntity.ok(reinforcementSignalPreviewService.generatePreview());
    }
}
