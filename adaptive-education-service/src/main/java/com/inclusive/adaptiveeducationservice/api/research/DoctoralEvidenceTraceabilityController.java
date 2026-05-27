package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.DoctoralEvidenceTraceabilityResponse;
import com.inclusive.adaptiveeducationservice.research.service.DoctoralEvidenceTraceabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/evidence")
@RequiredArgsConstructor
public class DoctoralEvidenceTraceabilityController {

    private final DoctoralEvidenceTraceabilityService doctoralEvidenceTraceabilityService;

    @GetMapping("/traceability-preview")
    public ResponseEntity<DoctoralEvidenceTraceabilityResponse> traceabilityPreview() {

        return ResponseEntity.ok(
                doctoralEvidenceTraceabilityService.generateTraceabilityPreview()
        );
    }
}
