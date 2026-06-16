package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.CiCdPipelineReadinessResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CiCdPipelineReadinessService {

    public CiCdPipelineReadinessResponse generateCiCdPreview() {

        return new CiCdPipelineReadinessResponse(
                "PIPELINE_READY_FOR_AUTOMATION",
                "GRADLE_BUILD_VALIDATED",
                "UNIT_AND_ARCHITECTURE_TESTS_ENABLED",
                "JACOCO_SPOTBUGS_AND_SONARCLOUD_READY",
                "DEPLOYMENT_WORKFLOW_PENDING_CONTAINERIZATION",
                List.of(
                        "Checkout source code",
                        "Set up Java 17",
                        "Restore Gradle cache",
                        "Run Gradle build",
                        "Execute tests",
                        "Generate Jacoco coverage report",
                        "Run static analysis",
                        "Publish quality evidence",
                        "Prepare deployment artifact"
                ),
                "Create Azure DevOps or GitHub Actions pipeline with build, test, Jacoco, SpotBugs, SonarCloud and artifact publishing stages."
        );
    }
}
