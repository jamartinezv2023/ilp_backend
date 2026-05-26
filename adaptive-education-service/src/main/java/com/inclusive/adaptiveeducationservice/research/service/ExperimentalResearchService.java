package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.ExperimentalDesignPreviewResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExperimentalResearchService {

    public ExperimentalDesignPreviewResponse generateExperimentalDesignPreview() {

        return new ExperimentalDesignPreviewResponse(

                "Applied mixed-method research",

                "Pre-experimental longitudinal educational validation",

                "Inclusive educational classrooms",

                "Intentional pedagogical sampling",

                List.of(
                        "Explainability",
                        "Teacher usability",
                        "Support coherence",
                        "Adaptive effectiveness"
                ),

                "Educational non-clinical AI policy active"
        );
    }
}
