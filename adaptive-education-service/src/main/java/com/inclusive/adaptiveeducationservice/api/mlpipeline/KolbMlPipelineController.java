package com.inclusive.adaptiveeducationservice.api.mlpipeline;

import com.inclusive.adaptiveeducationservice.mlpipeline.dto.KolbBaselineEvaluationResponse;
import com.inclusive.adaptiveeducationservice.mlpipeline.service.KolbBaselineMlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ml/kolb")
public class KolbMlPipelineController {

    private final KolbBaselineMlService kolbBaselineMlService;

    @PostMapping("/baseline/evaluate")
    public ResponseEntity<KolbBaselineEvaluationResponse>
    evaluateBaseline() {

        return ResponseEntity.ok(
                kolbBaselineMlService.evaluateBaseline()
        );
    }
}