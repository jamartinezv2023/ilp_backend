package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalTransparencyResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationalTransparencyService {

    public EducationalTransparencyResponse generateTransparencyPreview() {

        return new EducationalTransparencyResponse(
                "HIGH_TRANSPARENCY",
                "ACTIVE",
                List.of(
                        "Educational purpose of the AI output",
                        "Primary pedagogical factors",
                        "Non-clinical decision boundary",
                        "Human review requirement",
                        "Recommended inclusive support action"
                ),
                "AVAILABLE",
                "Teachers and institutions must be informed that AI outputs are assistive, explainable and non-diagnostic.",
                "CONTROLLED"
        );
    }
}
