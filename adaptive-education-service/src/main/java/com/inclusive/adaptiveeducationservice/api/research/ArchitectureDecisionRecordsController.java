package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.ArchitectureDecisionRecordsResponse;
import com.inclusive.adaptiveeducationservice.research.service.ArchitectureDecisionRecordsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/architecture")
@RequiredArgsConstructor
public class ArchitectureDecisionRecordsController {

    private final ArchitectureDecisionRecordsService architectureDecisionRecordsService;

    @GetMapping("/adr-preview")
    public ResponseEntity<ArchitectureDecisionRecordsResponse>
    adrPreview() {

        return ResponseEntity.ok(
                architectureDecisionRecordsService.generateAdrPreview()
        );
    }
}
