package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.GovernancePolicyPreviewResponse;
import com.inclusive.adaptiveeducationservice.research.service.GovernanceResearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/governance")
@RequiredArgsConstructor
public class GovernanceResearchController {

    private final GovernanceResearchService governanceResearchService;

    @GetMapping("/policy-preview")
    public ResponseEntity<GovernancePolicyPreviewResponse>
    policyPreview() {

        return ResponseEntity.ok(
                governanceResearchService.generateGovernancePreview()
        );
    }
}
