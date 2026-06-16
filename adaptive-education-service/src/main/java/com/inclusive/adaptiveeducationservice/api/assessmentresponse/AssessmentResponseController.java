package com.inclusive.adaptiveeducationservice.api.assessmentresponse;

import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentResponseRequest;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentResponseResponse;
import com.inclusive.adaptiveeducationservice.assessmentresponse.service.AssessmentResponseService;
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
@RequestMapping("/api/v1/assessment-responses")
public class AssessmentResponseController {

    private final AssessmentResponseService responseService;

    @PostMapping
    public ResponseEntity<AssessmentResponseResponse> submit(
            @Valid @RequestBody AssessmentResponseRequest request
    ) {
        return ResponseEntity.ok(responseService.submit(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentResponseResponse> findById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(responseService.findById(id));
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<AssessmentResponseResponse>> findByStudentId(
            @PathVariable String studentId
    ) {
        return ResponseEntity.ok(responseService.findByStudentId(studentId));
    }
}