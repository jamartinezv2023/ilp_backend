package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.TrustworthinessAssessmentResponse;
import org.springframework.stereotype.Service;

@Service
public class TrustworthinessResearchService {

    public TrustworthinessAssessmentResponse generateTrustworthinessAssessment() {

        return new TrustworthinessAssessmentResponse(

                "HIGH",

                "VALIDATED",

                "ACTIVE",

                "MANDATORY",

                "CONTROLLED",

                "STABLE"
        );
    }
}
