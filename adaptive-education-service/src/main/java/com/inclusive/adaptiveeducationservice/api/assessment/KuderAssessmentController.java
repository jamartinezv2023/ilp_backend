package com.inclusive.adaptiveeducationservice.api.assessment;

import com.inclusive.adaptiveeducationservice.assessment.dto.KuderAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessment.dto.KuderAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessment.service.KuderAssessmentService;
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
@RequestMapping("/api/v1/assessments/kuder")
public class KuderAssessmentController {

    private final KuderAssessmentService kuderAssessmentService;
    private final LegacyAssessmentWritePolicy legacyWritePolicy;

    @PostMapping
    public ResponseEntity<KuderAssessmentResponse> submit(
            @Valid @RequestBody KuderAssessmentRequest request
    ) {
        legacyWritePolicy.rejectNewSubmission("Kuder");
        throw new IllegalStateException("Legacy write policy must reject submission");
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<KuderAssessmentResponse>> findByStudentId(
            @PathVariable String studentId
    ) {
        return ResponseEntity.ok(
                kuderAssessmentService.findByStudentId(studentId)
        );
    }
}
