package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.KubernetesReadinessResponse;
import com.inclusive.adaptiveeducationservice.research.service.KubernetesReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/deployment")
@RequiredArgsConstructor
public class KubernetesReadinessController {

    private final KubernetesReadinessService kubernetesReadinessService;

    @GetMapping("/kubernetes-readiness-preview")
    public ResponseEntity<KubernetesReadinessResponse>
    kubernetesReadinessPreview() {

        return ResponseEntity.ok(
                kubernetesReadinessService.generateKubernetesReadinessPreview()
        );
    }
}
