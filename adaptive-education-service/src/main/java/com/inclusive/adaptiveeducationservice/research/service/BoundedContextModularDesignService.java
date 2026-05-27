package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.BoundedContextModularDesignResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoundedContextModularDesignService {

    public BoundedContextModularDesignResponse generateBoundedContextPreview() {

        return new BoundedContextModularDesignResponse(
                "HIGH_MODULAR_COHERENCE",
                "BOUNDED_CONTEXTS_IDENTIFIED_AND_EVOLVING",
                List.of(
                        "Adaptive education intelligence",
                        "Psychopedagogical profiling",
                        "Inclusive support planning",
                        "Research validation intelligence",
                        "Quality and architecture governance",
                        "Ethical AI governance"
                ),
                "RESPONSIBILITIES_SEPARATED_BY_API_DTO_SERVICE_MODULE_BOUNDARIES",
                "ALIGNED_WITH_INCLUSIVE_EDUCATIONAL_INTELLIGENCE_DOMAIN",
                "Document bounded contexts, context map, integration boundaries and architecture decision records."
        );
    }
}
