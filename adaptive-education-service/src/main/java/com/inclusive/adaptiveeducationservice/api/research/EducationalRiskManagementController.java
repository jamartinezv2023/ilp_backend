package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalRiskManagementResponse;
import com.inclusive.adaptiveeducationservice.research.service.EducationalRiskManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/risk-management")
@RequiredArgsConstructor
public class EducationalRiskManagementController {

    private final EducationalRiskManagementService educationalRiskManagementService;

    @GetMapping("/educational-preview")
    public ResponseEntity<EducationalRiskManagementResponse>
    educationalPreview() {

        return ResponseEntity.ok(
                educationalRiskManagementService.generateEducationalRiskManagement()
        );
    }
}
