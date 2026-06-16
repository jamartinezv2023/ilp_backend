package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.ArchitectureRemediationRoadmapResponse;
import com.inclusive.adaptiveeducationservice.research.service.ArchitectureRemediationRoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/architecture")
@RequiredArgsConstructor
public class ArchitectureRemediationRoadmapController {

    private final ArchitectureRemediationRoadmapService architectureRemediationRoadmapService;

    @GetMapping("/remediation-roadmap-preview")
    public ResponseEntity<ArchitectureRemediationRoadmapResponse>
    remediationRoadmapPreview() {

        return ResponseEntity.ok(
                architectureRemediationRoadmapService.generateRemediationRoadmapPreview()
        );
    }
}
