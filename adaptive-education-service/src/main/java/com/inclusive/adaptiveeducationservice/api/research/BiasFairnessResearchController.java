package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.BiasFairnessAssessmentResponse;
import com.inclusive.adaptiveeducationservice.research.service.BiasFairnessResearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/fairness")
@RequiredArgsConstructor
public class BiasFairnessResearchController {

    private final BiasFairnessResearchService biasFairnessResearchService;

    @GetMapping("/bias-assessment-preview")
    public ResponseEntity<BiasFairnessAssessmentResponse> biasAssessmentPreview() {

        return ResponseEntity.ok(
                biasFairnessResearchService.generateBiasFairnessAssessment()
        );
    }
}
