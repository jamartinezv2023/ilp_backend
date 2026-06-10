package com.inclusive.adaptiveeducationservice.api.researchanalytics;

import com.inclusive.adaptiveeducationservice.researchanalytics.dto.KolbDistributionResponse;
import com.inclusive.adaptiveeducationservice.researchanalytics.dto.KolbStatisticsResponse;
import com.inclusive.adaptiveeducationservice.researchanalytics.dto.ResearchSummaryResponse;
import com.inclusive.adaptiveeducationservice.researchanalytics.service.ResearchAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/analytics/research")
public class ResearchAnalyticsController {

    private final ResearchAnalyticsService researchAnalyticsService;

    @GetMapping("/kolb/distribution")
    public ResponseEntity<KolbDistributionResponse> kolbDistribution() {
        return ResponseEntity.ok(
                researchAnalyticsService.kolbDistribution()
        );
    }

    @GetMapping("/kolb/statistics")
    public ResponseEntity<KolbStatisticsResponse> kolbStatistics() {
        return ResponseEntity.ok(
                researchAnalyticsService.kolbStatistics()
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<ResearchSummaryResponse> researchSummary() {
        return ResponseEntity.ok(
                researchAnalyticsService.researchSummary()
        );
    }
}