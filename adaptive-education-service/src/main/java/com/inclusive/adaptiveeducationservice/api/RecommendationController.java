package com.inclusive.adaptiveeducationservice.api;

import com.inclusive.adaptiveeducationservice.domain.adaptation.LearningRecommendation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    @GetMapping("/student/{studentId}")
    public List<LearningRecommendation> byStudent(@PathVariable Long studentId) {
        // Mock temporal para validar compilaciÃ³n
        return new ArrayList<>();
    }
}
