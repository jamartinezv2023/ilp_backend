package com.inclusive.adaptiveeducationservice.api;

import com.inclusive.adaptiveeducationservice.domain.adaptation.LearningRecommendation;
import com.inclusive.adaptiveeducationservice.domain.adaptation.repository.LearningRecommendationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final LearningRecommendationRepository repo;

    public RecommendationController(LearningRecommendationRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/student/{studentId}")
    public List<LearningRecommendation> byStudent(@PathVariable Long studentId) {
        return repo.findByStudentIdOrderByCreatedAtDesc(studentId);
    }
}
