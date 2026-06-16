package com.inclusive.adaptiveeducationservice.assessment.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KolbAssessmentEngine {

    public KolbScores calculate(List<Integer> answers) {
        validateAnswers(answers);

        int scoreCE = 0;
        int scoreRO = 0;
        int scoreAC = 0;
        int scoreAE = 0;

        for (int index = 0; index < answers.size(); index += 4) {
            scoreCE += answers.get(index);
            scoreRO += answers.get(index + 1);
            scoreAC += answers.get(index + 2);
            scoreAE += answers.get(index + 3);
        }

        var learningStyle = determineLearningStyle(
                scoreCE,
                scoreRO,
                scoreAC,
                scoreAE
        );

        return new KolbScores(
                scoreCE,
                scoreRO,
                scoreAC,
                scoreAE,
                learningStyle
        );
    }

    private void validateAnswers(List<Integer> answers) {
        if (answers == null || answers.size() != 48) {
            throw new IllegalArgumentException(
                    "Kolb assessment requires exactly 48 answers"
            );
        }

        boolean invalidAnswer = answers.stream()
                .anyMatch(answer -> answer == null || answer < 1 || answer > 4);

        if (invalidAnswer) {
            throw new IllegalArgumentException(
                    "Each Kolb answer must be between 1 and 4"
            );
        }
    }

    private String determineLearningStyle(
            int scoreCE,
            int scoreRO,
            int scoreAC,
            int scoreAE
    ) {
        int abstractConcreteAxis = scoreAC - scoreCE;
        int activeReflectiveAxis = scoreAE - scoreRO;

        if (abstractConcreteAxis < 0 && activeReflectiveAxis < 0) {
            return "DIVERGENT";
        }

        if (abstractConcreteAxis >= 0 && activeReflectiveAxis < 0) {
            return "ASSIMILATING";
        }

        if (abstractConcreteAxis >= 0 && activeReflectiveAxis >= 0) {
            return "CONVERGENT";
        }

        return "ACCOMMODATING";
    }

    public record KolbScores(
            Integer scoreCE,
            Integer scoreRO,
            Integer scoreAC,
            Integer scoreAE,
            String learningStyle
    ) {
    }
}