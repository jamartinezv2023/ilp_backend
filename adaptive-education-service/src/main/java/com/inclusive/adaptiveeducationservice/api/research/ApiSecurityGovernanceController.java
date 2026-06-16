package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.ApiSecurityGovernanceResponse;
import com.inclusive.adaptiveeducationservice.research.service.ApiSecurityGovernanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/openapi")
@RequiredArgsConstructor
public class ApiSecurityGovernanceController {

    private final ApiSecurityGovernanceService apiSecurityGovernanceService;

    @GetMapping("/security-governance-preview")
    public ResponseEntity<ApiSecurityGovernanceResponse>
    securityGovernancePreview() {

        return ResponseEntity.ok(
                apiSecurityGovernanceService.generateSecurityGovernancePreview()
        );
    }
}
