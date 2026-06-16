package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalSustainabilityResponse;
import com.inclusive.adaptiveeducationservice.research.service.EducationalSustainabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/sustainability")
@RequiredArgsConstructor
public class EducationalSustainabilityController {

    private final EducationalSustainabilityService educationalSustainabilityService;

    @GetMapping("/educational-preview")
    public ResponseEntity<EducationalSustainabilityResponse>
    educationalPreview() {

        return ResponseEntity.ok(
                educationalSustainabilityService.generateSustainabilityPreview()
        );
    }
}
