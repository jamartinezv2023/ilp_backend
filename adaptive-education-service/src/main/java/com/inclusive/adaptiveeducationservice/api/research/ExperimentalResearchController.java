package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.ExperimentalDesignPreviewResponse;
import com.inclusive.adaptiveeducationservice.research.service.ExperimentalResearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/research")
@RequiredArgsConstructor
public class ExperimentalResearchController {

    private final ExperimentalResearchService experimentalResearchService;

    @GetMapping("/experimental-design-preview")
    public ResponseEntity<ExperimentalDesignPreviewResponse>
    experimentalDesignPreview() {

        return ResponseEntity.ok(
                experimentalResearchService.generateExperimentalDesignPreview()
        );
    }
}
