package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.DoctoralReviewPackageResponse;
import com.inclusive.adaptiveeducationservice.research.service.DoctoralReviewPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/doctoral")
@RequiredArgsConstructor
public class DoctoralReviewPackageController {

    private final DoctoralReviewPackageService doctoralReviewPackageService;

    @GetMapping("/review-package-preview")
    public ResponseEntity<DoctoralReviewPackageResponse> reviewPackagePreview() {

        return ResponseEntity.ok(
                doctoralReviewPackageService.generateReviewPackagePreview()
        );
    }
}
