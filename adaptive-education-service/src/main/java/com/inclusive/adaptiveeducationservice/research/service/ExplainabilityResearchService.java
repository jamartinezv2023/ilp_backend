package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.ExplainabilityPreviewResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExplainabilityResearchService {

    public ExplainabilityPreviewResponse generateExplainabilityPreview() {

        return new ExplainabilityPreviewResponse(

                "SUPPORT-001",

                List.of(
                        "Low engagement evolution",
                        "Reflective learning preference",
                        "High support intensity"
                ),

                "Structured multimodal reflective intervention",

                "HIGH",

                "VALIDATED"
        );
    }
}
