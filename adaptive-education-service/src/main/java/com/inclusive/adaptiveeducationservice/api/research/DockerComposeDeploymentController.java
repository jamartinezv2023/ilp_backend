package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.DockerComposeDeploymentResponse;
import com.inclusive.adaptiveeducationservice.research.service.DockerComposeDeploymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/deployment")
@RequiredArgsConstructor
public class DockerComposeDeploymentController {

    private final DockerComposeDeploymentService dockerComposeDeploymentService;

    @GetMapping("/docker-compose-preview")
    public ResponseEntity<DockerComposeDeploymentResponse>
    dockerComposePreview() {

        return ResponseEntity.ok(
                dockerComposeDeploymentService.generateDockerComposePreview()
        );
    }
}
