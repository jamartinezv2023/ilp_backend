package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.DevSecOpsQualityResponse;
import com.inclusive.adaptiveeducationservice.research.service.DevSecOpsQualityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/quality")
@RequiredArgsConstructor
public class DevSecOpsQualityController {

    private final DevSecOpsQualityService devSecOpsQualityService;

    @GetMapping("/devsecops-preview")
    public ResponseEntity<DevSecOpsQualityResponse>
    devSecOpsPreview() {

        return ResponseEntity.ok(
                devSecOpsQualityService.generateDevSecOpsPreview()
        );
    }
}
