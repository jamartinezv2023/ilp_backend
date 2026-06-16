package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.DeploymentReadinessResponse;
import com.inclusive.adaptiveeducationservice.research.service.DeploymentReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/quality")
@RequiredArgsConstructor
public class DeploymentReadinessController {

    private final DeploymentReadinessService deploymentReadinessService;

    @GetMapping("/deployment-readiness-preview")
    public ResponseEntity<DeploymentReadinessResponse>
    deploymentReadinessPreview() {

        return ResponseEntity.ok(
                deploymentReadinessService.generateDeploymentReadinessPreview()
        );
    }
}
