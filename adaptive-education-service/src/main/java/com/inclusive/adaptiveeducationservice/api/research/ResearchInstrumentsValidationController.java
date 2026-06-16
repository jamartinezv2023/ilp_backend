package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.ResearchInstrumentsValidationResponse;
import com.inclusive.adaptiveeducationservice.research.service.ResearchInstrumentsValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/research")
@RequiredArgsConstructor
public class ResearchInstrumentsValidationController {

    private final ResearchInstrumentsValidationService researchInstrumentsValidationService;

    @GetMapping("/instruments-validation-preview")
    public ResponseEntity<ResearchInstrumentsValidationResponse>
    instrumentsValidationPreview() {

        return ResponseEntity.ok(
                researchInstrumentsValidationService.generateInstrumentsValidationPreview()
        );
    }
}
