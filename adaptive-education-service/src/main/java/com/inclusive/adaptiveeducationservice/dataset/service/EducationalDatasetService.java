package com.inclusive.adaptiveeducationservice.dataset.service;

import com.inclusive.adaptiveeducationservice.dataset.dto.EducationalMlTrainingRowResponse;
import com.inclusive.adaptiveeducationservice.featurestore.entity.StudentFeatureEntity;
import com.inclusive.adaptiveeducationservice.featurestore.repository.StudentFeatureRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class EducationalDatasetService {

    private final StudentFeatureRepository studentFeatureRepository;

    public EducationalDatasetService(
            StudentFeatureRepository studentFeatureRepository
    ) {
        this.studentFeatureRepository = studentFeatureRepository;
    }

    public List<EducationalMlTrainingRowResponse> buildTrainingSnapshot() {
        return studentFeatureRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(
                        StudentFeatureEntity::getFeatureDate
                ).reversed())
                .map(this::toTrainingRow)
                .toList();
    }

    private EducationalMlTrainingRowResponse toTrainingRow(
            StudentFeatureEntity feature
    ) {
        return new EducationalMlTrainingRowResponse(
                feature.getStudentId(),
                feature.getFeatureDate(),
                feature.getKolbCe(),
                feature.getKolbRo(),
                feature.getKolbAc(),
                feature.getKolbAe(),
                feature.getKolbStyle()
        );
    }
}