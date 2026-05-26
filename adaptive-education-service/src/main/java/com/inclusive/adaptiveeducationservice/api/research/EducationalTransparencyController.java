package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalTransparencyResponse;
import com.inclusive.adaptiveeducationservice.research.service.EducationalTransparencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/transparency")
@RequiredArgsConstructor
public class EducationalTransparencyController {

    private final EducationalTransparencyService educationalTransparencyService;

    @GetMapping("/disclosure-preview")
    public ResponseEntity<EducationalTransparencyResponse>
    disclosurePreview() {

        return ResponseEntity.ok(
                educationalTransparencyService.generateTransparencyPreview()
        );
    }
}
