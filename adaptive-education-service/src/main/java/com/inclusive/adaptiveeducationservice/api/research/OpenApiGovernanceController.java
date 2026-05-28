package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.OpenApiGovernanceResponse;
import com.inclusive.adaptiveeducationservice.research.service.OpenApiGovernanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/openapi")
@RequiredArgsConstructor
public class OpenApiGovernanceController {

    private final OpenApiGovernanceService openApiGovernanceService;

    @GetMapping("/governance-preview")
    public ResponseEntity<OpenApiGovernanceResponse>
    governancePreview() {

        return ResponseEntity.ok(
                openApiGovernanceService.generateOpenApiGovernancePreview()
        );
    }
}
