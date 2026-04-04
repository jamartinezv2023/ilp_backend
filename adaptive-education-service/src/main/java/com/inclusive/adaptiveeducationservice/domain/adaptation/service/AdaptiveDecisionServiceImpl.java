package com.inclusive.adaptiveeducationservice.domain.adaptation.service;

import com.inclusive.adaptiveeducationservice.domain.adaptation.LearningRecommendation;
import com.inclusive.adaptiveeducationservice.domain.adaptation.decision.DecisionContext;
import com.inclusive.adaptiveeducationservice.domain.adaptation.decision.PolicyResult;
import com.inclusive.adaptiveeducationservice.domain.adaptation.decision.PolicySelector;
import com.inclusive.adaptiveeducationservice.domain.adaptation.log.DecisionLog;
import com.inclusive.adaptiveeducationservice.domain.adaptation.log.DecisionLogRepository;
import com.inclusive.adaptiveeducationservice.domain.adaptation.repository.LearningRecommendationRepository;
import com.inclusive.common.events.v1.AssessmentCompletedEvent;
import com.inclusive.common.events.v1.LearningPathRecommendedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AdaptiveDecisionServiceImpl implements AdaptiveDecisionService {

    private final PolicySelector selector;
    private final LearningRecommendationRepository recommendationRepository;
    private final DecisionLogRepository decisionLogRepository;
    private final ApplicationEventPublisher publisher;

    public AdaptiveDecisionServiceImpl(
            PolicySelector selector,
            LearningRecommendationRepository recommendationRepository,
            DecisionLogRepository decisionLogRepository,
            ApplicationEventPublisher publisher
    ) {
        this.selector = selector;
        this.recommendationRepository = recommendationRepository;
        this.decisionLogRepository = decisionLogRepository;
        this.publisher = publisher;
    }

    @Override
    public void handleAssessmentCompleted(AssessmentCompletedEvent event) {

        String tenantId = event.tenantId() == null ? "DEFAULT" : event.tenantId();

        DecisionContext context = new DecisionContext(
                event.studentId(),
                tenantId,
                String.valueOf(event.assessmentId()),
                event.type(),
                event.score(),
                event.result(),
                event.completedAt(),
                "BASELINE",   // luego lo controlaremos con A/B assignment real
                "policy-v1",
                "none"
        );

        PolicyResult chosen = selector.select(context);

        LearningRecommendation rec = new LearningRecommendation(
                context.getStudentId(),
                context.getTenantId(),
                context.getAssessmentType(),
                context.getScore(),
                context.getResult(),
                chosen.itemsAsCsv(),
                chosen.getRationale()
        );
        LearningRecommendation saved = recommendationRepository.save(rec);

        DecisionLog log = new DecisionLog(
                context.getStudentId(),
                context.getTenantId(),
                context.getAssessmentId(),
                context.getAssessmentType(),
                context.getScore(),
                context.getResult(),
                chosen.getPolicyName(),
                chosen.itemsAsCsv(),
                chosen.getRationale(),
                chosen.getConfidence(),
                chosen.traceAsText()
        );
        decisionLogRepository.save(log);

        // Emit learning-path event, preserving correlationId for end-to-end trace
        publisher.publishEvent(
                LearningPathRecommendedEvent.of(
                        event.correlationId(),
                        context.getStudentId(),
                        context.getTenantId(),
                        String.valueOf(saved.getId()),
                        chosen.getRationale(),
                        chosen.getRecommendedItems(),
                        chosen.getConfidence(),
                        chosen.getPolicyName(),
                        "none",      // modelName (Step 3 will fill this)
                        "none",      // modelVersion
                        Map.of(
                                "producer", "adaptive-education-service",
                                "policy_version", context.getPolicyVersion()
                        )
                )
        );
    }
}
