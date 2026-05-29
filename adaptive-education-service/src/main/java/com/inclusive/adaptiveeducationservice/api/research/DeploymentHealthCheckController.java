package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.DeploymentHealthCheckResponse;
import com.inclusive.adaptiveeducationservice.research.service.DeploymentHealthCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/deployment")
@RequiredArgsConstructor
public class DeploymentHealthCheckController {

    private final DeploymentHealthCheckService deploymentHealthCheckService;

    @GetMapping("/health-check-preview")
    public ResponseEntity<DeploymentHealthCheckResponse>
    healthCheckPreview() {

        return ResponseEntity.ok(
                deploymentHealthCheckService.generateHealthCheckPreview()
        );
    }
}
