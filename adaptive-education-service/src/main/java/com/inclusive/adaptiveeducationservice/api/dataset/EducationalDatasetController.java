package com.inclusive.adaptiveeducationservice.api.dataset;

import com.inclusive.adaptiveeducationservice.dataset.dto.EducationalMlTrainingRowResponse;
import com.inclusive.adaptiveeducationservice.dataset.service.EducationalDatasetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/datasets")
public class EducationalDatasetController {

    private final EducationalDatasetService datasetService;

    @GetMapping("/educational-ml/training-snapshot")
    public ResponseEntity<List<EducationalMlTrainingRowResponse>>
    trainingSnapshot() {
        return ResponseEntity.ok(datasetService.buildTrainingSnapshot());
    }
}