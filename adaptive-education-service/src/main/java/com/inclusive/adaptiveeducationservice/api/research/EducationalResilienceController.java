package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalResilienceResponse;
import com.inclusive.adaptiveeducationservice.research.service.EducationalResilienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/resilience")
@RequiredArgsConstructor
public class EducationalResilienceController {

    private final EducationalResilienceService educationalResilienceService;

    @GetMapping("/operational-preview")
    public ResponseEntity<EducationalResilienceResponse>
    operationalPreview() {

        return ResponseEntity.ok(
                educationalResilienceService.generateOperationalResilience()
        );
    }
}
