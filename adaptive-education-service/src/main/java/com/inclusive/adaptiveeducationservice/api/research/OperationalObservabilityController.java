package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.OperationalObservabilityResponse;
import com.inclusive.adaptiveeducationservice.research.service.OperationalObservabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/observability")
@RequiredArgsConstructor
public class OperationalObservabilityController {

    private final OperationalObservabilityService operationalObservabilityService;

    @GetMapping("/operational-preview")
    public ResponseEntity<OperationalObservabilityResponse>
    operationalPreview() {

        return ResponseEntity.ok(
                operationalObservabilityService.generateOperationalObservability()
        );
    }
}
