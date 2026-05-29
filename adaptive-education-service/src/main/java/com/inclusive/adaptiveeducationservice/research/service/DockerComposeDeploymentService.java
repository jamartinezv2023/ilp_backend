package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.DockerComposeDeploymentResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DockerComposeDeploymentService {

    public DockerComposeDeploymentResponse generateDockerComposePreview() {

        return new DockerComposeDeploymentResponse(
                "DOCKER_COMPOSE_READY_FOR_LOCAL_ORCHESTRATION",
                "BACKEND_AND_DATABASE_SERVICES_CAN_BE_ORCHESTRATED",
                "POSTGRESQL_SERVICE_REQUIRED",
                "ENVIRONMENT_VARIABLES_REQUIRED_FOR_DATASOURCE",
                "LOCAL_REPRODUCIBLE_DEPLOYMENT_READY_AFTER_COMPOSE_FILE",
                List.of(
                        "Dockerfile hardening preview implemented",
                        "Spring Boot bootJar artifact available",
                        "PostgreSQL runtime validated",
                        "Secure runtime configuration preview implemented",
                        "Secrets governance preview implemented",
                        "Deployment readiness preview implemented"
                ),
                "Create docker-compose.yml with adaptive-education-service and PostgreSQL services using environment variables and a persistent database volume."
        );
    }
}
