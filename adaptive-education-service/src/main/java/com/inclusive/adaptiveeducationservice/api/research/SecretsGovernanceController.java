package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.SecretsGovernanceResponse;
import com.inclusive.adaptiveeducationservice.research.service.SecretsGovernanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/security")
@RequiredArgsConstructor
public class SecretsGovernanceController {

    private final SecretsGovernanceService secretsGovernanceService;

    @GetMapping("/secrets-governance-preview")
    public ResponseEntity<SecretsGovernanceResponse>
    secretsGovernancePreview() {

        return ResponseEntity.ok(
                secretsGovernanceService.generateSecretsGovernancePreview()
        );
    }
}
