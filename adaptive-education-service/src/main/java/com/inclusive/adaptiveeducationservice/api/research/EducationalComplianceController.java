package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalComplianceResponse;
import com.inclusive.adaptiveeducationservice.research.service.EducationalComplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/compliance")
@RequiredArgsConstructor
public class EducationalComplianceController {

    private final EducationalComplianceService educationalComplianceService;

    @GetMapping("/educational-preview")
    public ResponseEntity<EducationalComplianceResponse>
    educationalPreview() {

        return ResponseEntity.ok(
                educationalComplianceService.generateEducationalCompliance()
        );
    }
}
