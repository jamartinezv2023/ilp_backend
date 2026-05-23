package com.inclusive.diagnosis.analytics.controller;

import com.inclusive.diagnosis.analytics.dto.LearningProfileSummaryResponse;
import com.inclusive.diagnosis.analytics.service.LearningProfileAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class LearningAnalyticsController {

    private final LearningProfileAggregationService
            aggregationService;

    @GetMapping("/student/{studentId}/learning-profile")
    public ResponseEntity<LearningProfileSummaryResponse>
    summarizeLearningProfile(
            @PathVariable UUID studentId
    ) {

        return ResponseEntity.ok(
                aggregationService.summarize(studentId)
        );
    }
}
