package com.inclusive.adaptiveeducationservice.student.config;

import com.inclusive.adaptiveeducationservice.student.entity.StudentProfileEntity;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentProfileDataSeeder implements CommandLineRunner {

    private final StudentProfileRepository studentProfileRepository;

    public StudentProfileDataSeeder(
            StudentProfileRepository studentProfileRepository
    ) {
        this.studentProfileRepository = studentProfileRepository;
    }

    @Override
    public void run(String... args) {
        if (studentProfileRepository.count() > 0) {
            return;
        }

        studentProfileRepository.saveAll(List.of(
                new StudentProfileEntity(
                        "ST-001",
                        "Mariana Gomez",
                        "6A",
                        11,
                        "Divergent learning profile",
                        "Arts and communication",
                        "MEDIUM",
                        List.of(
                                "Visual learning support",
                                "Collaborative classroom activities",
                                "Flexible demonstration of understanding"
                        ),
                        List.of(
                                "Use visual organizers before complex tasks",
                                "Promote peer-supported learning",
                                "Provide step-by-step instructions"
                        )
                ),
                new StudentProfileEntity(
                        "ST-002",
                        "Samuel Rodriguez",
                        "7B",
                        12,
                        "Sequential learning profile",
                        "Technology and problem solving",
                        "LOW",
                        List.of(
                                "Structured task sequencing",
                                "Clear evaluation criteria",
                                "Short feedback cycles"
                        ),
                        List.of(
                                "Provide ordered checklists",
                                "Use short practice activities",
                                "Offer immediate formative feedback"
                        )
                ),
                new StudentProfileEntity(
                        "ST-003",
                        "Valentina Torres",
                        "8A",
                        13,
                        "Reflective learning profile",
                        "Health and social care",
                        "HIGH",
                        List.of(
                                "Additional processing time",
                                "Emotionally safe participation options",
                                "Reasonable adjustment monitoring"
                        ),
                        List.of(
                                "Allow preparation time before oral participation",
                                "Use private formative feedback",
                                "Coordinate follow-up with inclusion team"
                        )
                )
        ));
    }
}