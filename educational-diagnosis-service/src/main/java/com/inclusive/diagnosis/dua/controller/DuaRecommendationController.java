package com.inclusive.diagnosis.dua.controller;

import com.inclusive.diagnosis.dua.dto.CreateDuaRecommendationRequest;
import com.inclusive.diagnosis.dua.entity.DuaRecommendation;
import com.inclusive.diagnosis.dua.repository.DuaRecommendationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dua-recommendations")
@RequiredArgsConstructor
public class DuaRecommendationController {

    private final DuaRecommendationRepository repository;

    @PostMapping
    public ResponseEntity<DuaRecommendation> create(
            @Valid @RequestBody
            CreateDuaRecommendationRequest request
    ) {

        DuaRecommendation recommendation =
                DuaRecommendation.builder()
                        .tenantId(request.tenantId())
                        .studentProfileId(
                                request.studentProfileId()
                        )
                        .duaPrinciple(
                                request.duaPrinciple()
                        )
                        .recommendationCategory(
                                request.recommendationCategory()
                        )
                        .recommendationText(
                                request.recommendationText()
                        )
                        .accessibilitySupport(
                                request.accessibilitySupport()
                        )
                        .assistiveTechnologySuggestion(
                                request.assistiveTechnologySuggestion()
                        )
                        .implementationGuidance(
                                request.implementationGuidance()
                        )
                        .priorityScore(
                                request.priorityScore()
                        )
                        .generatedAt(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(
                repository.save(recommendation)
        );
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<DuaRecommendation>>
    findByStudentId(
            @PathVariable UUID studentId
    ) {

        return ResponseEntity.ok(
                repository.findByStudentProfileId(studentId)
        );
    }
}
