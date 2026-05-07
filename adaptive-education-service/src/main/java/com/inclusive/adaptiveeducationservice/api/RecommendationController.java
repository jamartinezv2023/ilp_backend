package com.inclusive.adaptiveeducationservice.api;

import com.inclusive.adaptiveeducationservice.domain.adaptation.LearningRecommendation;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    @GetMapping("/student/{studentId}")
    public List<LearningRecommendation> byStudent(@PathVariable Long studentId) {
        // Mock temporal para validar compilación
        return new ArrayList<>();
    }
}
