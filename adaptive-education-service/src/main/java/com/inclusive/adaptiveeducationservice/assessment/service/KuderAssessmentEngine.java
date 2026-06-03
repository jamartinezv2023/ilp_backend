package com.inclusive.adaptiveeducationservice.assessment.service;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class KuderAssessmentEngine {

    private static final List<String> VALID_AREAS = List.of(
            "SCIENTIFIC",
            "ARTISTIC",
            "SOCIAL",
            "MECHANICAL",
            "ADMINISTRATIVE"
    );

    public KuderScores calculate(List<String> answers) {
        validateAnswers(answers);

        int scientificScore = 0;
        int artisticScore = 0;
        int socialScore = 0;
        int mechanicalScore = 0;
        int administrativeScore = 0;

        for (String rawAnswer : answers) {
            var answer = rawAnswer.trim().toUpperCase();

            if ("SCIENTIFIC".equals(answer)) {
                scientificScore++;
            } else if ("ARTISTIC".equals(answer)) {
                artisticScore++;
            } else if ("SOCIAL".equals(answer)) {
                socialScore++;
            } else if ("MECHANICAL".equals(answer)) {
                mechanicalScore++;
            } else {
                administrativeScore++;
            }
        }

        var scores = Map.of(
                "SCIENTIFIC", scientificScore,
                "ARTISTIC", artisticScore,
                "SOCIAL", socialScore,
                "MECHANICAL", mechanicalScore,
                "ADMINISTRATIVE", administrativeScore
        );

        var topAreas = scores.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(
                        Comparator.reverseOrder()
                ))
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        return new KuderScores(
                topAreas.get(0),
                topAreas,
                scientificScore,
                artisticScore,
                socialScore,
                mechanicalScore,
                administrativeScore
        );
    }

    private void validateAnswers(List<String> answers) {
        if (answers == null || answers.size() != 30) {
            throw new IllegalArgumentException(
                    "Kuder assessment requires exactly 30 answers"
            );
        }

        boolean invalidAnswer = answers.stream()
                .anyMatch(answer -> answer == null
                        || !VALID_AREAS.contains(answer.trim().toUpperCase()));

        if (invalidAnswer) {
            throw new IllegalArgumentException(
                    "Each Kuder answer must be a valid vocational area"
            );
        }
    }

    public record KuderScores(
            String dominantVocationalArea,
            List<String> topVocationalAreas,
            Integer scientificScore,
            Integer artisticScore,
            Integer socialScore,
            Integer mechanicalScore,
            Integer administrativeScore
    ) {
    }
}