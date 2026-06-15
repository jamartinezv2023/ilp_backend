package com.inclusive.adaptiveeducationservice.api.assessment;

import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessments/kolb")
public class KolbAssessmentController {

    private final KolbAssessmentService kolbAssessmentService;

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("status", "KOLB_CONTROLLER_OK"));
    }

    @PostMapping
    public ResponseEntity<KolbAssessmentResponse> submit(
            @Valid @RequestBody KolbAssessmentRequest request
    ) {
        return ResponseEntity.ok(kolbAssessmentService.submit(request));
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<KolbAssessmentResponse>> findByStudentId(
            @PathVariable String studentId
    ) {
        return ResponseEntity.ok(kolbAssessmentService.findByStudentId(studentId));
    }
}
