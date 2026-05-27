package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.DevSecOpsQualityResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DevSecOpsQualityService {

    public DevSecOpsQualityResponse generateDevSecOpsPreview() {

        return new DevSecOpsQualityResponse(

                "PARTIALLY_AUTOMATED",

                "SPOTBUGS_AND_SONARCLOUD_ENABLED",

                "JACOCO_XML_REPORTING_ENABLED",

                "DEPENDENCY_AND_RUNTIME_SECURITY_REVIEW_PENDING",

                "QUALITY_GATE_PENDING_FINAL_VALIDATION",

                List.of(
                        "Gradle build successful",
                        "SpotBugs enabled",
                        "SonarCloud integration available",
                        "Jacoco reporting enabled",
                        "Git traceability active",
                        "REST endpoints validated",
                        "PostgreSQL runtime validated"
                ),

                "Finalize SonarCloud Quality Gate, integrate dependency vulnerability scanning and prepare CI/CD pipeline evidence."
        );
    }
}
