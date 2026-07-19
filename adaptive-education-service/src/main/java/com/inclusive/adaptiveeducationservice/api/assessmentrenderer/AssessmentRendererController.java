package com.inclusive.adaptiveeducationservice.api.assessmentrenderer;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.GenericAssessmentDefinitionQueryService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.rendering.AssessmentRendererModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assessment-renderer")
public class AssessmentRendererController {

    private final GenericAssessmentDefinitionQueryService
            queryService;

    public AssessmentRendererController(
            GenericAssessmentDefinitionQueryService queryService
    ) {
        this.queryService = queryService;
    }

    @GetMapping("/{assessmentCode}")
    public ResponseEntity<AssessmentRendererModel> findByCode(
            @PathVariable String assessmentCode
    ) {
        return ResponseEntity.ok(
                queryService.findRendererModel(
                        assessmentCode
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<AssessmentRendererModel>>
    findAllActive() {
        return ResponseEntity.ok(
                queryService.findAllActiveRendererModels()
        );
    }
}