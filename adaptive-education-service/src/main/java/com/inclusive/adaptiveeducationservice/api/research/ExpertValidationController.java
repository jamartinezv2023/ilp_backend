package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.ExpertValidationResponse;
import com.inclusive.adaptiveeducationservice.research.service.ExpertValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/research")
@RequiredArgsConstructor
public class ExpertValidationController {

    private final ExpertValidationService expertValidationService;

    @GetMapping("/expert-validation-preview")
    public ResponseEntity<ExpertValidationResponse> expertValidationPreview() {

        return ResponseEntity.ok(
                expertValidationService.generateExpertValidationPreview()
        );
    }
}
