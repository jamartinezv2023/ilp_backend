package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.CiCdPipelineReadinessResponse;
import com.inclusive.adaptiveeducationservice.research.service.CiCdPipelineReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/quality")
@RequiredArgsConstructor
public class CiCdPipelineReadinessController {

    private final CiCdPipelineReadinessService ciCdPipelineReadinessService;

    @GetMapping("/cicd-preview")
    public ResponseEntity<CiCdPipelineReadinessResponse>
    ciCdPreview() {

        return ResponseEntity.ok(
                ciCdPipelineReadinessService.generateCiCdPreview()
        );
    }
}
