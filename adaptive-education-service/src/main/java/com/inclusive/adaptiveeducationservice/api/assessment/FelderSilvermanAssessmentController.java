package com.inclusive.adaptiveeducationservice.api.assessment;

import com.inclusive.adaptiveeducationservice.assessment.dto.FelderSilvermanAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessment.dto.FelderSilvermanAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessment.service.FelderSilvermanAssessmentService;
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
@RequestMapping("/api/v1/assessments/felder-silverman")
public class FelderSilvermanAssessmentController {

    private final FelderSilvermanAssessmentService assessmentService;

    @PostMapping
    public ResponseEntity<FelderSilvermanAssessmentResponse> submit(
            @Valid @RequestBody FelderSilvermanAssessmentRequest request
    ) {
        return ResponseEntity.ok(assessmentService.submit(request));
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<FelderSilvermanAssessmentResponse>>
    findByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(assessmentService.findByStudentId(studentId));
    }
}