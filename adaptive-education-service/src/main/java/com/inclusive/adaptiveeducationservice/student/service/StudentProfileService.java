package com.inclusive.adaptiveeducationservice.student.service;

import com.inclusive.adaptiveeducationservice.student.dto.StudentProfileResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentProfileService {

    private final List<StudentProfileResponse> students = List.of(
            new StudentProfileResponse(
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
            new StudentProfileResponse(
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
            new StudentProfileResponse(
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
    );

    public List<StudentProfileResponse> findAll() {
        return students;
    }

    public Optional<StudentProfileResponse> findById(String id) {
        return students.stream()
                .filter(student -> student.id().equalsIgnoreCase(id))
                .findFirst();
    }
}
