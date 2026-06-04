package com.inclusive.adaptiveeducationservice.api.intervention;

import com.inclusive.adaptiveeducationservice.intervention.dto.EducationalInterventionRequest;
import com.inclusive.adaptiveeducationservice.intervention.dto.EducationalInterventionResponse;
import com.inclusive.adaptiveeducationservice.intervention.service.EducationalInterventionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/interventions")
public class EducationalInterventionController {

    private final EducationalInterventionService interventionService;

    @PostMapping
    public ResponseEntity<EducationalInterventionResponse> create(
            @Valid @RequestBody EducationalInterventionRequest request
    ) {
        return ResponseEntity.ok(interventionService.create(request));
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<EducationalInterventionResponse>>
    findByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(interventionService.findByStudentId(studentId));
    }

    @PatchMapping("/{interventionId}/status")
    public ResponseEntity<EducationalInterventionResponse> updateStatus(
            @PathVariable String interventionId,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(
                interventionService.updateStatus(interventionId, status)
        );
    }
}