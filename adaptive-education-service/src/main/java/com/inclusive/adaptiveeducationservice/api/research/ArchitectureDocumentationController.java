package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.ArchitectureDocumentationResponse;
import com.inclusive.adaptiveeducationservice.research.service.ArchitectureDocumentationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/architecture")
@RequiredArgsConstructor
public class ArchitectureDocumentationController {

    private final ArchitectureDocumentationService architectureDocumentationService;

    @GetMapping("/documentation-preview")
    public ResponseEntity<ArchitectureDocumentationResponse>
    documentationPreview() {

        return ResponseEntity.ok(
                architectureDocumentationService.generateDocumentationPreview()
        );
    }
}
