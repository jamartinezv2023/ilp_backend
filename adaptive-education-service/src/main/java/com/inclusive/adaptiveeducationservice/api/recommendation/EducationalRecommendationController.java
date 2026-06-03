package com.inclusive.adaptiveeducationservice.api.recommendation;

import com.inclusive.adaptiveeducationservice.recommendation.dto.StudentRecommendationResponse;
import com.inclusive.adaptiveeducationservice.recommendation.service.EducationalRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendations")
public class EducationalRecommendationController {

    private final EducationalRecommendationService recommendationService;

    @GetMapping("/students/{studentId}")
    public ResponseEntity<StudentRecommendationResponse> generateForStudent(
            @PathVariable String studentId
    ) {
        return ResponseEntity.ok(
                recommendationService.generateForStudent(studentId)
        );
    }
}