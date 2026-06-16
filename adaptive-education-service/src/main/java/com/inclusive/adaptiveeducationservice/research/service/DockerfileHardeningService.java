package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.DockerfileHardeningResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DockerfileHardeningService {

    public DockerfileHardeningResponse generateDockerfilePreview() {

        return new DockerfileHardeningResponse(
                "DOCKERFILE_REQUIRED_FOR_REPRODUCIBLE_DEPLOYMENT",
                "JAVA_17_RUNTIME_IMAGE_RECOMMENDED",
                "NON_ROOT_RUNTIME_USER_RECOMMENDED",
                "DATASOURCE_AND_PORT_ENVIRONMENT_VARIABLES_REQUIRED",
                "SPRING_BOOT_JAR_ARTIFACT_DEPLOYMENT",
                List.of(
                        "Gradle build produces Spring Boot artifact",
                        "PostgreSQL runtime validated",
                        "Deployment readiness preview implemented",
                        "Secure runtime configuration preview implemented",
                        "Secrets governance preview implemented",
                        "Repository hygiene hardening started"
                ),
                "Create a Dockerfile using Java 17 runtime, copy the bootJar artifact, expose port 8083 and externalize datasource configuration through environment variables."
        );
    }
}
