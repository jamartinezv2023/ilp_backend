package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalComplianceResponse;
import org.springframework.stereotype.Service;

@Service
public class EducationalComplianceService {

    public EducationalComplianceResponse generateEducationalCompliance() {

        return new EducationalComplianceResponse(

                "INSTITUTIONALLY_ALIGNED",

                "VALIDATED",

                "ACTIVE",

                "MANDATORY",

                "ENABLED",

                "CONFIRMED"
        );
    }
}
