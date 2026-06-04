package com.inclusive.adaptiveeducationservice.instrument.config;

import com.inclusive.adaptiveeducationservice.instrument.entity.InstrumentQuestionEntity;
import com.inclusive.adaptiveeducationservice.instrument.repository.InstrumentQuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InstrumentQuestionSeeder implements CommandLineRunner {

    private final InstrumentQuestionRepository questionRepository;

    public InstrumentQuestionSeeder(
            InstrumentQuestionRepository questionRepository
    ) {
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(String... args) {
        if (questionRepository.count() > 0) {
            return;
        }

        questionRepository.saveAll(List.of(
                new InstrumentQuestionEntity(
                        "KOLB-Q001",
                        "KOLB",
                        "CE",
                        1,
                        "When learning something new, I prefer concrete experiences.",
                        List.of("1", "2", "3", "4"),
                        "KOLB_BASELINE_V1"
                ),
                new InstrumentQuestionEntity(
                        "KOLB-Q002",
                        "KOLB",
                        "RO",
                        2,
                        "When learning, I prefer observing and reflecting before acting.",
                        List.of("1", "2", "3", "4"),
                        "KOLB_BASELINE_V1"
                ),
                new InstrumentQuestionEntity(
                        "KOLB-Q003",
                        "KOLB",
                        "AC",
                        3,
                        "When learning, I prefer abstract concepts and explanations.",
                        List.of("1", "2", "3", "4"),
                        "KOLB_BASELINE_V1"
                ),
                new InstrumentQuestionEntity(
                        "KOLB-Q004",
                        "KOLB",
                        "AE",
                        4,
                        "When learning, I prefer experimenting and applying ideas.",
                        List.of("1", "2", "3", "4"),
                        "KOLB_BASELINE_V1"
                ),
                new InstrumentQuestionEntity(
                        "FS-Q001",
                        "FELDER_SILVERMAN",
                        "ACTIVE_REFLECTIVE",
                        1,
                        "I understand something better after I try it out.",
                        List.of("A", "B"),
                        "FELDER_SILVERMAN_BASELINE_V1"
                ),
                new InstrumentQuestionEntity(
                        "FS-Q002",
                        "FELDER_SILVERMAN",
                        "SENSING_INTUITIVE",
                        2,
                        "I prefer learning facts rather than theories.",
                        List.of("A", "B"),
                        "FELDER_SILVERMAN_BASELINE_V1"
                ),
                new InstrumentQuestionEntity(
                        "FS-Q003",
                        "FELDER_SILVERMAN",
                        "VISUAL_VERBAL",
                        3,
                        "I remember better what I see than what I hear.",
                        List.of("A", "B"),
                        "FELDER_SILVERMAN_BASELINE_V1"
                ),
                new InstrumentQuestionEntity(
                        "FS-Q004",
                        "FELDER_SILVERMAN",
                        "SEQUENTIAL_GLOBAL",
                        4,
                        "I learn better in small sequential steps.",
                        List.of("A", "B"),
                        "FELDER_SILVERMAN_BASELINE_V1"
                ),
                new InstrumentQuestionEntity(
                        "KUDER-Q001",
                        "KUDER",
                        "VOCATIONAL_AREA",
                        1,
                        "Which area is most interesting for you?",
                        List.of(
                                "SCIENTIFIC",
                                "ARTISTIC",
                                "SOCIAL",
                                "MECHANICAL",
                                "ADMINISTRATIVE"
                        ),
                        "KUDER_BASELINE_V1"
                )
        ));
    }
}