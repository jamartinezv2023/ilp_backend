package com.inclusive.adaptiveeducationservice.api.mlmodels;

import com.inclusive.adaptiveeducationservice.mlmodels.dto.RandomForestEvaluationResponse;
import com.inclusive.adaptiveeducationservice.mlmodels.service.RandomForestKolbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ml/kolb/random-forest")
public class RandomForestKolbController {

    private final RandomForestKolbService randomForestKolbService;

    @PostMapping("/evaluate")
    public ResponseEntity<RandomForestEvaluationResponse> evaluate() {
        return ResponseEntity.ok(randomForestKolbService.evaluate());
    }
}