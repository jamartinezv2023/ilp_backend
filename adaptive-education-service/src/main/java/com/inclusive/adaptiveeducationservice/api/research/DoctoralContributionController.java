package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.DoctoralContributionResponse;
import com.inclusive.adaptiveeducationservice.research.service.DoctoralContributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/doctoral")
@RequiredArgsConstructor
public class DoctoralContributionController {

    private final DoctoralContributionService doctoralContributionService;

    @GetMapping("/contribution-preview")
    public ResponseEntity<DoctoralContributionResponse> contributionPreview() {

        return ResponseEntity.ok(
                doctoralContributionService.generateContributionPreview()
        );
    }
}
