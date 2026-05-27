package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.PilotValidationReadinessResponse;
import com.inclusive.adaptiveeducationservice.research.service.PilotValidationReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/pilot")
@RequiredArgsConstructor
public class PilotValidationReadinessController {

    private final PilotValidationReadinessService pilotValidationReadinessService;

    @GetMapping("/validation-readiness-preview")
    public ResponseEntity<PilotValidationReadinessResponse> validationReadinessPreview() {

        return ResponseEntity.ok(
                pilotValidationReadinessService.generatePilotValidationReadiness()
        );
    }
}
