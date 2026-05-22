package com.inclusive.diagnosis.diagnosis.controller;

import com.inclusive.diagnosis.diagnosis.dto.CreateInclusiveDiagnosisRequest;
import com.inclusive.diagnosis.diagnosis.entity.InclusiveDiagnosis;
import com.inclusive.diagnosis.diagnosis.repository.InclusiveDiagnosisRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/diagnoses")
@RequiredArgsConstructor
public class InclusiveDiagnosisController {

    private final InclusiveDiagnosisRepository repository;

    @PostMapping
    public ResponseEntity<InclusiveDiagnosis> create(
            @Valid @RequestBody
            CreateInclusiveDiagnosisRequest request
    ) {

        InclusiveDiagnosis diagnosis =
                InclusiveDiagnosis.builder()
                        .tenantId(request.tenantId())
                        .studentProfileId(
                                request.studentProfileId()
                        )
                        .diagnosisCategory(
                                request.diagnosisCategory()
                        )
                        .diagnosisSummary(
                                request.diagnosisSummary()
                        )
                        .identifiedBarriers(
                                request.identifiedBarriers()
                        )
                        .learningStrengths(
                                request.learningStrengths()
                        )
                        .supportNeeds(
                                request.supportNeeds()
                        )
                        .recommendedInterventions(
                                request.recommendedInterventions()
                        )
                        .dueAlignment(
                                request.dueAlignment()
                        )
                        .confidenceScore(
                                request.confidenceScore()
                        )
                        .generatedAt(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(
                repository.save(diagnosis)
        );
    }
}
