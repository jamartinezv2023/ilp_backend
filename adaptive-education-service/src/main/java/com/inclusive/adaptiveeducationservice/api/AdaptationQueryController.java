package com.inclusive.adaptiveeducationservice.api;

import com.inclusive.adaptiveeducationservice.domain.adaptation.LearningRecommendation;
import com.inclusive.adaptiveeducationservice.domain.adaptation.repository.LearningRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/adaptation")
@RequiredArgsConstructor
public class AdaptationQueryController {

    private final LearningRecommendationRepository repository;

    @GetMapping("/history/{studentId}")
    public List<LearningRecommendation> getStudentHistory(@PathVariable Long studentId) {
        return repository.findByStudentId(studentId);
    }
}
