package com.inclusive.diagnosis.indicator.controller;

import com.inclusive.diagnosis.indicator.dto.CreateLearningIndicatorRequest;
import com.inclusive.diagnosis.indicator.entity.LearningIndicator;
import com.inclusive.diagnosis.indicator.repository.LearningIndicatorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/indicators")
@RequiredArgsConstructor
public class LearningIndicatorController {

    private final LearningIndicatorRepository repository;

    @PostMapping
    public ResponseEntity<LearningIndicator> create(
            @Valid @RequestBody
            CreateLearningIndicatorRequest request
    ) {

        LearningIndicator indicator =
                LearningIndicator.builder()
                        .tenantId(request.tenantId())
                        .studentProfileId(
                                request.studentProfileId()
                        )
                        .indicatorCode(
                                request.indicatorCode()
                        )
                        .indicatorCategory(
                                request.indicatorCategory()
                        )
                        .indicatorValue(
                                request.indicatorValue()
                        )
                        .interpretation(
                                request.interpretation()
                        )
                        .pedagogicalRecommendation(
                                request.pedagogicalRecommendation()
                        )
                        .calculatedAt(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(
                repository.save(indicator)
        );
    }
}
