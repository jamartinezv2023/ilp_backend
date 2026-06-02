package com.inclusive.adaptiveeducationservice.assessment.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FelderSilvermanAssessmentEngine {

    public FelderSilvermanScores calculate(List<String> answers) {
        validateAnswers(answers);

        int activeReflective = 0;
        int sensingIntuitive = 0;
        int visualVerbal = 0;
        int sequentialGlobal = 0;

        for (int index = 0; index < answers.size(); index++) {
            var answer = answers.get(index).trim().toUpperCase();
            var value = "A".equals(answer) ? 1 : -1;

            var dimension = index % 4;

            if (dimension == 0) {
                activeReflective += value;
            } else if (dimension == 1) {
                sensingIntuitive += value;
            } else if (dimension == 2) {
                visualVerbal += value;
            } else {
                sequentialGlobal += value;
            }
        }

        var preferences = resolvePreferences(
                activeReflective,
                sensingIntuitive,
                visualVerbal,
                sequentialGlobal
        );

        return new FelderSilvermanScores(
                activeReflective,
                sensingIntuitive,
                visualVerbal,
                sequentialGlobal,
                String.join("_", preferences),
                preferences
        );
    }

    private void validateAnswers(List<String> answers) {
        if (answers == null || answers.size() != 44) {
            throw new IllegalArgumentException(
                    "Felder-Silverman assessment requires exactly 44 answers"
            );
        }

        boolean invalidAnswer = answers.stream()
                .anyMatch(answer -> answer == null
                        || !List.of("A", "B").contains(
                        answer.trim().toUpperCase()
                ));

        if (invalidAnswer) {
            throw new IllegalArgumentException(
                    "Each Felder-Silverman answer must be A or B"
            );
        }
    }

    private List<String> resolvePreferences(
            int activeReflective,
            int sensingIntuitive,
            int visualVerbal,
            int sequentialGlobal
    ) {
        var preferences = new ArrayList<String>();

        preferences.add(activeReflective >= 0 ? "ACTIVE" : "REFLECTIVE");
        preferences.add(sensingIntuitive >= 0 ? "SENSING" : "INTUITIVE");
        preferences.add(visualVerbal >= 0 ? "VISUAL" : "VERBAL");
        preferences.add(sequentialGlobal >= 0 ? "SEQUENTIAL" : "GLOBAL");

        return preferences;
    }

    public record FelderSilvermanScores(
            Integer activeReflectiveScore,
            Integer sensingIntuitiveScore,
            Integer visualVerbalScore,
            Integer sequentialGlobalScore,
            String dominantProfile,
            List<String> learningPreferences
    ) {
    }
}