package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalAdoptionReadinessResponse;
import com.inclusive.adaptiveeducationservice.research.service.EducationalAdoptionReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/adoption")
@RequiredArgsConstructor
public class EducationalAdoptionReadinessController {

    private final EducationalAdoptionReadinessService educationalAdoptionReadinessService;

    @GetMapping("/readiness-preview")
    public ResponseEntity<EducationalAdoptionReadinessResponse> readinessPreview() {

        return ResponseEntity.ok(
                educationalAdoptionReadinessService.generateAdoptionReadiness()
        );
    }
}
