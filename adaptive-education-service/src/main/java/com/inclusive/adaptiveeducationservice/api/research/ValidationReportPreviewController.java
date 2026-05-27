package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.ValidationReportPreviewResponse;
import com.inclusive.adaptiveeducationservice.research.service.ValidationReportPreviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/validation")
@RequiredArgsConstructor
public class ValidationReportPreviewController {

    private final ValidationReportPreviewService validationReportPreviewService;

    @GetMapping("/report-preview")
    public ResponseEntity<ValidationReportPreviewResponse> reportPreview() {

        return ResponseEntity.ok(
                validationReportPreviewService.generateValidationReportPreview()
        );
    }
}
