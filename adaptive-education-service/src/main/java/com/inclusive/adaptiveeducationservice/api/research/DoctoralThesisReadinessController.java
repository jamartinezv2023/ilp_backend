package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.DoctoralThesisReadinessResponse;
import com.inclusive.adaptiveeducationservice.research.service.DoctoralThesisReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/doctoral")
@RequiredArgsConstructor
public class DoctoralThesisReadinessController {

    private final DoctoralThesisReadinessService doctoralThesisReadinessService;

    @GetMapping("/thesis-readiness-preview")
    public ResponseEntity<DoctoralThesisReadinessResponse> thesisReadinessPreview() {

        return ResponseEntity.ok(
                doctoralThesisReadinessService.generateThesisReadinessPreview()
        );
    }
}
