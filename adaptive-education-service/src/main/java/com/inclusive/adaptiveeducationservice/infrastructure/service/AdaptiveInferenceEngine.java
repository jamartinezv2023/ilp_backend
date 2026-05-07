package com.inclusive.adaptiveeducationservice.infrastructure.service;
import com.inclusive.adaptiveeducationservice.domain.adaptation.repository.AdaptationRuleRepository;
import com.inclusive.adaptiveeducationservice.domain.model.StudentCharacterization;
import com.inclusive.adaptiveeducationservice.infrastructure.strategy.InferenceStrategy;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdaptiveInferenceEngine {
    private final List<InferenceStrategy> strategies;
    private final AdaptationRuleRepository ruleRepository;

    public AdaptiveInferenceEngine(List<InferenceStrategy> strategies, AdaptationRuleRepository ruleRepository) {
        this.strategies = strategies;
        this.ruleRepository = ruleRepository;
    }

    public List<String> inferAdjustments(StudentCharacterization sc) {
        return strategies.stream()
            .filter(s -> s.appliesTo(sc.getLearningStyles()))
            .map(s -> {
                return ruleRepository.findByStyleKeyAndStyleValue("kolb", s.getStyleKey())
                    .map(rule -> rule.getAdjustmentContent())
                    .orElse("Ajuste base para " + s.getStyleKey());
            })
            .collect(Collectors.toList());
    }
}
