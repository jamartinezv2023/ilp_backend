package com.inclusive.adaptiveeducationservice.api.assessmentdefinition;

import com.inclusive.adaptiveeducationservice.assessmentdefinition.dto.AssessmentDefinitionResponse;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.service.AssessmentDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessment-definitions")
public class AssessmentDefinitionController {

    private final AssessmentDefinitionService assessmentDefinitionService;

    @GetMapping
    public ResponseEntity<List<AssessmentDefinitionResponse>> findAll() {
        return ResponseEntity.ok(assessmentDefinitionService.findAll());
    }

    @GetMapping("/{code}")
    public ResponseEntity<AssessmentDefinitionResponse> findByCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(
                assessmentDefinitionService.findActiveByCode(code)
        );
    }
}