package com.inclusive.adaptiveeducationservice.api.adaptive;

import com.inclusive.adaptiveeducationservice.adaptive.dto.AdaptiveLearningPlanResponse;
import com.inclusive.adaptiveeducationservice.adaptive.service.AdaptiveLearningPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/adaptive")
public class AdaptiveLearningPlanController {

    private final AdaptiveLearningPlanService adaptiveLearningPlanService;

    @GetMapping("/students/{studentId}")
    public ResponseEntity<AdaptiveLearningPlanResponse> generateForStudent(
            @PathVariable String studentId
    ) {
        return ResponseEntity.ok(
                adaptiveLearningPlanService.generateForStudent(studentId)
        );
    }

    @GetMapping("/students/{studentId}/history")
    public ResponseEntity<List<AdaptiveLearningPlanResponse>> historyByStudent(
            @PathVariable String studentId
    ) {
        return ResponseEntity.ok(
                adaptiveLearningPlanService.historyByStudent(studentId)
        );
    }
}