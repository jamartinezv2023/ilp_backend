package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.HumanCenteredAssistanceResponse;
import com.inclusive.adaptiveeducationservice.research.service.HumanCenteredAssistanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/human-centered")
@RequiredArgsConstructor
public class HumanCenteredAssistanceController {

    private final HumanCenteredAssistanceService humanCenteredAssistanceService;

    @GetMapping("/assistance-preview")
    public ResponseEntity<HumanCenteredAssistanceResponse>
    assistancePreview() {

        return ResponseEntity.ok(
                humanCenteredAssistanceService.generateHumanCenteredAssistance()
        );
    }
}
