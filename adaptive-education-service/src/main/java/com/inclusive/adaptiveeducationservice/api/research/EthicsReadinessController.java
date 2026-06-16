package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.EthicsReadinessResponse;
import com.inclusive.adaptiveeducationservice.research.service.EthicsReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/research")
@RequiredArgsConstructor
public class EthicsReadinessController {

    private final EthicsReadinessService ethicsReadinessService;

    @GetMapping("/ethics-readiness-preview")
    public ResponseEntity<EthicsReadinessResponse> ethicsReadinessPreview() {

        return ResponseEntity.ok(
                ethicsReadinessService.generateEthicsReadinessPreview()
        );
    }
}
