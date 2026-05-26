package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalDataProtectionResponse;
import com.inclusive.adaptiveeducationservice.research.service.EducationalDataProtectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/data-protection")
@RequiredArgsConstructor
public class EducationalDataProtectionController {

    private final EducationalDataProtectionService educationalDataProtectionService;

    @GetMapping("/privacy-preview")
    public ResponseEntity<EducationalDataProtectionResponse>
    privacyPreview() {

        return ResponseEntity.ok(
                educationalDataProtectionService.generatePrivacyPreview()
        );
    }
}
