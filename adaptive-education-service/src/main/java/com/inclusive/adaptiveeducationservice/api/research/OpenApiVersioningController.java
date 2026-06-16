package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.OpenApiVersioningResponse;
import com.inclusive.adaptiveeducationservice.research.service.OpenApiVersioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/openapi")
@RequiredArgsConstructor
public class OpenApiVersioningController {

    private final OpenApiVersioningService openApiVersioningService;

    @GetMapping("/versioning-preview")
    public ResponseEntity<OpenApiVersioningResponse>
    versioningPreview() {

        return ResponseEntity.ok(
                openApiVersioningService.generateVersioningPreview()
        );
    }
}
