package com.inclusive.adaptiveeducationservice.researchanalytics.service;

import com.inclusive.adaptiveeducationservice.featurestore.entity.StudentFeatureEntity;
import com.inclusive.adaptiveeducationservice.featurestore.repository.StudentFeatureRepository;
import com.inclusive.adaptiveeducationservice.researchanalytics.dto.KolbDistributionResponse;
import com.inclusive.adaptiveeducationservice.researchanalytics.dto.KolbStatisticsResponse;
import com.inclusive.adaptiveeducationservice.researchanalytics.dto.ResearchSummaryResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResearchAnalyticsService {

    private final StudentFeatureRepository studentFeatureRepository;

    public ResearchAnalyticsService(StudentFeatureRepository studentFeatureRepository) {
        this.studentFeatureRepository = studentFeatureRepository;
    }

    public KolbDistributionResponse kolbDistribution() {
        var features = studentFeatureRepository.findAll();

        var distribution = features.stream()
                .collect(Collectors.groupingBy(
                        StudentFeatureEntity::getKolbStyle,
                        Collectors.counting()
                ));

        return new KolbDistributionResponse(features.size(), distribution);
    }

    public KolbStatisticsResponse kolbStatistics() {
        var features = studentFeatureRepository.findAll();

        if (features.isEmpty()) {
            return new KolbStatisticsResponse(0, 0.0, 0.0, 0.0, 0.0);
        }

        return new KolbStatisticsResponse(
                features.size(),
                averageCe(features),
                averageRo(features),
                averageAc(features),
                averageAe(features)
        );
    }

    public ResearchSummaryResponse researchSummary() {
        var features = studentFeatureRepository.findAll();

        var distribution = features.stream()
                .collect(Collectors.groupingBy(
                        StudentFeatureEntity::getKolbStyle,
                        Collectors.counting()
                ));

        var uniqueStudents = features.stream()
                .map(StudentFeatureEntity::getStudentId)
                .distinct()
                .count();

        return new ResearchSummaryResponse(
                features.size(),
                Math.toIntExact(uniqueStudents),
                distribution,
                datasetReadinessStatus(features.size(), uniqueStudents),
                recommendedNextAction(features.size(), uniqueStudents)
        );
    }

    private Double averageCe(List<StudentFeatureEntity> features) {
        return features.stream()
                .mapToInt(StudentFeatureEntity::getKolbCe)
                .average()
                .orElse(0.0);
    }

    private Double averageRo(List<StudentFeatureEntity> features) {
        return features.stream()
                .mapToInt(StudentFeatureEntity::getKolbRo)
                .average()
                .orElse(0.0);
    }

    private Double averageAc(List<StudentFeatureEntity> features) {
        return features.stream()
                .mapToInt(StudentFeatureEntity::getKolbAc)
                .average()
                .orElse(0.0);
    }

    private Double averageAe(List<StudentFeatureEntity> features) {
        return features.stream()
                .mapToInt(StudentFeatureEntity::getKolbAe)
                .average()
                .orElse(0.0);
    }

    private String datasetReadinessStatus(int featureRows, long uniqueStudents) {
        if (featureRows >= 1000 && uniqueStudents >= 300) {
            return "READY_FOR_INITIAL_ML_EXPERIMENTS";
        }

        if (featureRows >= 100 && uniqueStudents >= 50) {
            return "PROMISING_FOR_PILOT_ANALYTICS";
        }

        if (featureRows > 0) {
            return "EARLY_DATA_COLLECTION_STAGE";
        }

        return "NO_FEATURES_AVAILABLE";
    }

    private String recommendedNextAction(int featureRows, long uniqueStudents) {
        if (featureRows == 0) {
            return "Generate features from assessment responses before analytics.";
        }

        if (featureRows < 100 || uniqueStudents < 50) {
            return "Continue collecting real assessment responses and rebuilding feature store.";
        }

        return "Review class balance, missing values and sampling strategy before model training.";
    }
}