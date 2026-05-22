package com.inclusive.diagnosis.questionnaire.controller;

import com.inclusive.diagnosis.questionnaire.dto.CreateDiagnosticQuestionnaireRequest;
import com.inclusive.diagnosis.questionnaire.entity.DiagnosticQuestionnaire;
import com.inclusive.diagnosis.questionnaire.repository.DiagnosticQuestionnaireRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/questionnaires")
@RequiredArgsConstructor
public class DiagnosticQuestionnaireController {

    private final DiagnosticQuestionnaireRepository repository;

    @PostMapping
    public ResponseEntity<DiagnosticQuestionnaire> create(
            @Valid @RequestBody
            CreateDiagnosticQuestionnaireRequest request
    ) {

        DiagnosticQuestionnaire questionnaire =
                DiagnosticQuestionnaire.builder()
                        .tenantId(request.tenantId())
                        .title(request.title())
                        .questionnaireType(
                                request.questionnaireType()
                        )
                        .targetPopulation(
                                request.targetPopulation()
                        )
                        .active(request.active() != null ? request.active() : true)
                        .description(request.description())
                        .educationalLevel(
                                request.educationalLevel()
                        )
                        .accessibilityConsiderations(
                                request.accessibilityConsiderations()
                        )
                        .dueAlignmentNotes(
                                request.dueAlignmentNotes()
                        )
                        .createdAt(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(
                repository.save(questionnaire)
        );
    }
}
