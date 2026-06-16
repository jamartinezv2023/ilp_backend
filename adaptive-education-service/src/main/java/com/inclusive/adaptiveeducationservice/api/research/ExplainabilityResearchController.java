package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.ExplainabilityPreviewResponse;
import com.inclusive.adaptiveeducationservice.research.service.ExplainabilityResearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/xai")
@RequiredArgsConstructor
public class ExplainabilityResearchController {

    private final ExplainabilityResearchService explainabilityResearchService;

    @GetMapping("/explainability-preview")
    public ResponseEntity<ExplainabilityPreviewResponse>
    explainabilityPreview() {

        return ResponseEntity.ok(
                explainabilityResearchService.generateExplainabilityPreview()
        );
    }
}
