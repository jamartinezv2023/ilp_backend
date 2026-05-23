package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.LearningProfileSummaryResponse;
import com.inclusive.diagnosis.response.entity.DiagnosticResponse;
import com.inclusive.diagnosis.response.repository.DiagnosticResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LearningProfileAggregationService {

    private final DiagnosticResponseRepository responseRepository;

    public LearningProfileSummaryResponse summarize(
            UUID studentProfileId
    ) {

        var responses = responseRepository.findByStudentProfileId(
                studentProfileId
        );

        long reflective = countByValue(
                responses,
                "REFLECTIVE_OBSERVATION"
        );
        long active = countByValue(
                responses,
                "ACTIVE_EXPERIMENTATION"
        );
        long abstractive = countByValue(
                responses,
                "ABSTRACT_CONCEPTUALIZATION"
        );
        long concrete = countByValue(
                responses,
                "CONCRETE_EXPERIENCE"
        );

        long total = reflective + active + abstractive + concrete;

        Map<String, Long> scores = Map.of(
                "REFLECTIVE_LEARNER", reflective,
                "ACTIVE_LEARNER", active,
                "ABSTRACT_LEARNER", abstractive,
                "CONCRETE_LEARNER", concrete
        );

        var dominant = scores.entrySet()
                .stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("UNDETERMINED");

        double confidence = total == 0
                ? 0.0
                : scores.get(dominant) / (double) total;

        return new LearningProfileSummaryResponse(
                dominant,
                reflective,
                active,
                abstractive,
                concrete,
                confidence
        );
    }

    private long countByValue(
            Iterable<DiagnosticResponse> responses,
            String value
    ) {

        long count = 0;

        for (DiagnosticResponse response : responses) {
            if (value.equals(response.getResponseValue())) {
                count++;
            }
        }

        return count;
    }
}
