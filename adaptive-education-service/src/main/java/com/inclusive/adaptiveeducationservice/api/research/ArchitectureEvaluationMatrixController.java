package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.ArchitectureEvaluationMatrixResponse;
import com.inclusive.adaptiveeducationservice.research.service.ArchitectureEvaluationMatrixService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/architecture")
@RequiredArgsConstructor
public class ArchitectureEvaluationMatrixController {

    private final ArchitectureEvaluationMatrixService architectureEvaluationMatrixService;

    @GetMapping("/evaluation-matrix-preview")
    public ResponseEntity<ArchitectureEvaluationMatrixResponse>
    evaluationMatrixPreview() {

        return ResponseEntity.ok(
                architectureEvaluationMatrixService.generateEvaluationMatrixPreview()
        );
    }
}
