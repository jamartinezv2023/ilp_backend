package com.inclusive.adaptiveeducationservice.api.assessment;

import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessments/kolb")
public class KolbAssessmentController {

    private final KolbAssessmentService kolbAssessmentService;

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
        return ResponseEntity.ok(
                kolbAssessmentService.findByStudentId(studentId)
        );
    }
}