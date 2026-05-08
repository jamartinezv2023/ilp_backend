package com.inclusive.adaptiveeducationservice.infrastructure.messaging;

import com.inclusive.adaptiveeducationservice.domain.adaptation.LearningRecommendation;
import com.inclusive.adaptiveeducationservice.domain.adaptation.repository.LearningRecommendationRepository;
import com.inclusive.common.events.v1.AssessmentCompletedEvent;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
public class SmokeTestHandler {

    @Autowired
    private AssessmentCompletedEventHandler handler;

    @Autowired
    private LearningRecommendationRepository repository;

    @ParameterizedTest
    @ValueSource(strings = {"CONVERGENT", "DIVERGENT", "ASSIMILATOR", "ACCOMMODATOR"})
    public void validateAllKolbStrategies(String style) {
        repository.deleteAll();

        AssessmentCompletedEvent event = new AssessmentCompletedEvent();
        event.setStudentId(2026L);
        event.setResult(style);
        
        handler.onMessage(event);
        
        List<LearningRecommendation> recommendations = repository.findAll();
        
        assertFalse(recommendations.isEmpty(), "Falla en estrategia: " + style);
        System.out.println("SUCESO SQA: Estilo [" + style + "] -> " + recommendations.get(0).getContent());
    }
}
