package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.OpenApiArtifactExportResponse;
import com.inclusive.adaptiveeducationservice.research.service.OpenApiArtifactExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/openapi")
@RequiredArgsConstructor
public class OpenApiArtifactExportController {

    private final OpenApiArtifactExportService openApiArtifactExportService;

    @GetMapping("/artifact-preview")
    public ResponseEntity<OpenApiArtifactExportResponse>
    artifactPreview() {

        return ResponseEntity.ok(
                openApiArtifactExportService.generateArtifactExportPreview()
        );
    }
}
