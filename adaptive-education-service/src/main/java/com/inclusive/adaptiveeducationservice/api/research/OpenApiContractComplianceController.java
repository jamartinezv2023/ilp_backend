package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.OpenApiContractComplianceResponse;
import com.inclusive.adaptiveeducationservice.research.service.OpenApiContractComplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/quality")
@RequiredArgsConstructor
public class OpenApiContractComplianceController {

    private final OpenApiContractComplianceService openApiContractComplianceService;

    @GetMapping("/openapi-contract-preview")
    public ResponseEntity<OpenApiContractComplianceResponse>
    openApiContractPreview() {

        return ResponseEntity.ok(
                openApiContractComplianceService.generateOpenApiContractPreview()
        );
    }
}
