package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.ValidationPreviewResponse;
import com.inclusive.adaptiveeducationservice.research.service.ValidationResearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/research")
@RequiredArgsConstructor
public class ValidationResearchController {

    private final ValidationResearchService validationResearchService;

    @GetMapping("/validation-preview")
    public ResponseEntity<ValidationPreviewResponse> validationPreview() {

        return ResponseEntity.ok(
                validationResearchService.generateValidationPreview()
        );
    }
}
