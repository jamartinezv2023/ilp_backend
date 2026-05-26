package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.OperationalObservabilityResponse;
import org.springframework.stereotype.Service;

@Service
public class OperationalObservabilityService {

    public OperationalObservabilityResponse generateOperationalObservability() {

        return new OperationalObservabilityResponse(

                "FULL_TRACEABILITY",

                "ACTIVE",

                "ENABLED",

                "AVAILABLE",

                "ACTIVE",

                "CONTROLLED"
        );
    }
}
