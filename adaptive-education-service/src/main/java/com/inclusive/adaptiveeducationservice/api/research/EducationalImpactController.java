package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalImpactResponse;
import com.inclusive.adaptiveeducationservice.research.service.EducationalImpactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/impact")
@RequiredArgsConstructor
public class EducationalImpactController {

    private final EducationalImpactService educationalImpactService;

    @GetMapping("/educational-preview")
    public ResponseEntity<EducationalImpactResponse> educationalPreview() {

        return ResponseEntity.ok(
                educationalImpactService.generateEducationalImpact()
        );
    }
}
