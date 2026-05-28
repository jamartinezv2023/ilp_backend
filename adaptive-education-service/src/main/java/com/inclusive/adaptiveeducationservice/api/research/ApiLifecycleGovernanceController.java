package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.ApiLifecycleGovernanceResponse;
import com.inclusive.adaptiveeducationservice.research.service.ApiLifecycleGovernanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/openapi")
@RequiredArgsConstructor
public class ApiLifecycleGovernanceController {

    private final ApiLifecycleGovernanceService apiLifecycleGovernanceService;

    @GetMapping("/lifecycle-preview")
    public ResponseEntity<ApiLifecycleGovernanceResponse>
    lifecyclePreview() {

        return ResponseEntity.ok(
                apiLifecycleGovernanceService.generateLifecyclePreview()
        );
    }
}
