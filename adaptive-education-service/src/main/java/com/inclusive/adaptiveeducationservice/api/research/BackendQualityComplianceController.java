package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.BackendQualityComplianceResponse;
import com.inclusive.adaptiveeducationservice.research.service.BackendQualityComplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/quality")
@RequiredArgsConstructor
public class BackendQualityComplianceController {

    private final BackendQualityComplianceService backendQualityComplianceService;

    @GetMapping("/backend-compliance-preview")
    public ResponseEntity<BackendQualityComplianceResponse> backendCompliancePreview() {

        return ResponseEntity.ok(
                backendQualityComplianceService.generateBackendCompliancePreview()
        );
    }
}
