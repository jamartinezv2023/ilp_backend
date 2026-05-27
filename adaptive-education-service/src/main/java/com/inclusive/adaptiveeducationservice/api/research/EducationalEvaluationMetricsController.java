package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalEvaluationMetricsResponse;
import com.inclusive.adaptiveeducationservice.research.service.EducationalEvaluationMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/evaluation")
@RequiredArgsConstructor
public class EducationalEvaluationMetricsController {

    private final EducationalEvaluationMetricsService educationalEvaluationMetricsService;

    @GetMapping("/metrics-preview")
    public ResponseEntity<EducationalEvaluationMetricsResponse> metricsPreview() {

        return ResponseEntity.ok(
                educationalEvaluationMetricsService.generateEvaluationMetricsPreview()
        );
    }
}
