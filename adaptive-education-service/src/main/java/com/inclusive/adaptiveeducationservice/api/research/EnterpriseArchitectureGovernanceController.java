package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.EnterpriseArchitectureGovernanceResponse;
import com.inclusive.adaptiveeducationservice.research.service.EnterpriseArchitectureGovernanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/architecture")
@RequiredArgsConstructor
public class EnterpriseArchitectureGovernanceController {

    private final EnterpriseArchitectureGovernanceService enterpriseArchitectureGovernanceService;

    @GetMapping("/governance-preview")
    public ResponseEntity<EnterpriseArchitectureGovernanceResponse>
    governancePreview() {

        return ResponseEntity.ok(
                enterpriseArchitectureGovernanceService.generateGovernancePreview()
        );
    }
}
