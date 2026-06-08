package com.inclusive.adaptiveeducationservice.featurestore.service;

import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.featurestore.dto.StudentFeatureResponse;
import com.inclusive.adaptiveeducationservice.featurestore.entity.StudentFeatureEntity;
import com.inclusive.adaptiveeducationservice.featurestore.extractor.KolbFeatureExtractor;
import com.inclusive.adaptiveeducationservice.featurestore.repository.StudentFeatureRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class FeatureStoreService {

    private final StudentFeatureRepository featureRepository;

    private final AssessmentResponseRepository responseRepository;

    private final KolbFeatureExtractor kolbFeatureExtractor;

    public FeatureStoreService(
            StudentFeatureRepository featureRepository,
            AssessmentResponseRepository responseRepository,
            KolbFeatureExtractor kolbFeatureExtractor
    ) {
        this.featureRepository = featureRepository;
        this.responseRepository = responseRepository;
        this.kolbFeatureExtractor = kolbFeatureExtractor;
    }

    public StudentFeatureResponse rebuildStudentFeatures(
            String studentId
    ) {

        var responses =
                responseRepository.findByStudentIdOrderBySubmittedAtDesc(
                        studentId
                );

        var kolb =
                kolbFeatureExtractor.extract(responses);

        var feature = new StudentFeatureEntity(
                "SF-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase(),
                studentId,
                Instant.now(),
                kolb.ce(),
                kolb.ro(),
                kolb.ac(),
                kolb.ae(),
                kolb.style()
        );

        return toResponse(
                featureRepository.save(feature)
        );
    }

    public List<StudentFeatureResponse> findByStudentId(
            String studentId
    ) {
        return featureRepository
                .findByStudentIdOrderByFeatureDateDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private StudentFeatureResponse toResponse(
            StudentFeatureEntity entity
    ) {
        return new StudentFeatureResponse(
                entity.getId(),
                entity.getStudentId(),
                entity.getFeatureDate(),
                entity.getKolbCe(),
                entity.getKolbRo(),
                entity.getKolbAc(),
                entity.getKolbAe(),
                entity.getKolbStyle()
        );
    }
}