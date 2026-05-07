package com.inclusive.adaptiveeducationservice.infrastructure.messaging;
import com.inclusive.adaptiveeducationservice.domain.adaptation.LearningRecommendation;
import com.inclusive.adaptiveeducationservice.domain.adaptation.repository.LearningRecommendationRepository;
import com.inclusive.adaptiveeducationservice.domain.model.StudentCharacterization;
import com.inclusive.adaptiveeducationservice.infrastructure.service.AdaptiveInferenceEngine;
import com.inclusive.common.events.v1.AssessmentCompletedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.List;

@Component
public class AssessmentCompletedEventHandler {
    private final AdaptiveInferenceEngine inferenceEngine;
    private final LearningRecommendationRepository repository;

    public AssessmentCompletedEventHandler(AdaptiveInferenceEngine engine, LearningRecommendationRepository repo) {
        this.inferenceEngine = engine;
        this.repository = repo;
    }

    @Transactional
    public void onMessage(AssessmentCompletedEvent event) {
        StudentCharacterization sc = new StudentCharacterization();
        sc.setStudentId(String.valueOf(event.getStudentId()));
        sc.setLearningStyles(Map.of("kolb", event.getResult()));
        
        List<String> adjustments = inferenceEngine.inferAdjustments(sc);
        
        for (String content : adjustments) {
            LearningRecommendation rec = new LearningRecommendation();
            rec.setStudentId(event.getStudentId());
            rec.setContent(content);
            repository.save(rec);
        }
    }
}
