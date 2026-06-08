package com.inclusive.adaptiveeducationservice.api.featurestore;

import com.inclusive.adaptiveeducationservice.featurestore.dto.StudentFeatureResponse;
import com.inclusive.adaptiveeducationservice.featurestore.service.FeatureStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feature-store")
public class FeatureStoreController {

    private final FeatureStoreService featureStoreService;

    @PostMapping("/students/{studentId}/rebuild")
    public ResponseEntity<StudentFeatureResponse> rebuild(
            @PathVariable String studentId
    ) {
        return ResponseEntity.ok(
                featureStoreService.rebuildStudentFeatures(studentId)
        );
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<StudentFeatureResponse>> findByStudent(
            @PathVariable String studentId
    ) {
        return ResponseEntity.ok(
                featureStoreService.findByStudentId(studentId)
        );
    }
}