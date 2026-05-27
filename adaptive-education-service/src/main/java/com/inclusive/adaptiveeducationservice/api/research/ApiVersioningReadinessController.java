package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.ApiVersioningReadinessResponse;
import com.inclusive.adaptiveeducationservice.research.service.ApiVersioningReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/quality")
@RequiredArgsConstructor
public class ApiVersioningReadinessController {

    private final ApiVersioningReadinessService apiVersioningReadinessService;

    @GetMapping("/api-versioning-preview")
    public ResponseEntity<ApiVersioningReadinessResponse>
    apiVersioningPreview() {

        return ResponseEntity.ok(
                apiVersioningReadinessService.generateApiVersioningPreview()
        );
    }
}
