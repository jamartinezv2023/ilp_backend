package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.TrustworthinessAssessmentResponse;
import com.inclusive.adaptiveeducationservice.research.service.TrustworthinessResearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/trustworthiness")
@RequiredArgsConstructor
public class TrustworthinessResearchController {

    private final TrustworthinessResearchService trustworthinessResearchService;

    @GetMapping("/assessment-preview")
    public ResponseEntity<TrustworthinessAssessmentResponse>
    assessmentPreview() {

        return ResponseEntity.ok(
                trustworthinessResearchService.generateTrustworthinessAssessment()
        );
    }
}
