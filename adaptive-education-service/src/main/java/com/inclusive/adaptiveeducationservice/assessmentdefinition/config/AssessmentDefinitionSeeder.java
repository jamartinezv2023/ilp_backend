package com.inclusive.adaptiveeducationservice.assessmentdefinition.config;

import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentDefinitionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentOptionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentQuestionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class AssessmentDefinitionSeeder implements CommandLineRunner {

    private final AssessmentDefinitionRepository repository;

    public AssessmentDefinitionSeeder(
            AssessmentDefinitionRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.existsByCode("KOLB_V1")) {
            return;
        }

        var kolb = new AssessmentDefinitionEntity(
                "DEF-KOLB-V1",
                "KOLB_V1",
                "Kolb Learning Style Inventory",
                "Ipsative learning style inventory for experiential learning classification.",
                "IPSATIVE_RANKING",
                "1.0",
                true,
                20,
                "Rank each group of four statements from 4 to 1. Use 4 for the statement that best describes you and 1 for the statement that least describes you. Do not repeat values in the same group.",
                Instant.now()
        );

        for (int index = 1; index <= 12; index++) {
            var question = new AssessmentQuestionEntity(
                    "KOLB-V1-Q" + String.format("%03d", index),
                    index,
                    "Rank the four statements according to how you usually learn. Group "
                            + index + ".",
                    "CE_RO_AC_AE",
                    "Assign 4, 3, 2 and 1 exactly once.",
                    true,
                    "IPSATIVE_RANKING",
                    index
            );

            question.addOption(new AssessmentOptionEntity(
                    "KOLB-V1-Q" + String.format("%03d", index) + "-CE",
                    "I learn through concrete experiences.",
                    "CE",
                    0,
                    1
            ));
            question.addOption(new AssessmentOptionEntity(
                    "KOLB-V1-Q" + String.format("%03d", index) + "-RO",
                    "I learn by observing and reflecting.",
                    "RO",
                    0,
                    2
            ));
            question.addOption(new AssessmentOptionEntity(
                    "KOLB-V1-Q" + String.format("%03d", index) + "-AC",
                    "I learn through concepts and explanations.",
                    "AC",
                    0,
                    3
            ));
            question.addOption(new AssessmentOptionEntity(
                    "KOLB-V1-Q" + String.format("%03d", index) + "-AE",
                    "I learn by experimenting and applying ideas.",
                    "AE",
                    0,
                    4
            ));

            kolb.addQuestion(question);
        }

        repository.saveAll(List.of(kolb));
    }
}