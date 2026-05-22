package com.inclusive.diagnosis.response.controller;

import com.inclusive.diagnosis.engine.service.EducationalRuleEngineService;
import com.inclusive.diagnosis.response.dto.CreateDiagnosticResponseRequest;
import com.inclusive.diagnosis.response.entity.DiagnosticResponse;
import com.inclusive.diagnosis.response.repository.DiagnosticResponseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/responses")
@RequiredArgsConstructor
public class DiagnosticResponseController {

    private final DiagnosticResponseRepository repository;

    private final EducationalRuleEngineService
            educationalRuleEngineService;

    @PostMapping
    public ResponseEntity<DiagnosticResponse> create(
            @Valid @RequestBody
            CreateDiagnosticResponseRequest request
    ) {

        DiagnosticResponse response =
                DiagnosticResponse.builder()
                        .tenantId(request.tenantId())
                        .studentProfileId(
                                request.studentProfileId()
                        )
                        .questionnaireId(
                                request.questionnaireId()
                        )
                        .questionCode(request.questionCode())
                        .responseValue(request.responseValue())
                        .interpretationNotes(
                                request.interpretationNotes()
                        )
                        .submittedAt(LocalDateTime.now())
                        .build();

        DiagnosticResponse saved =
                repository.save(response);

        educationalRuleEngineService.processResponse(
                saved
        );

        return ResponseEntity.ok(saved);
    }
}
