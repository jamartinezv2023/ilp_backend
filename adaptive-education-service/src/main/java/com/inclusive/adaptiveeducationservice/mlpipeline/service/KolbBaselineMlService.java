package com.inclusive.adaptiveeducationservice.mlpipeline.service;

import com.inclusive.adaptiveeducationservice.featurestore.entity.StudentFeatureEntity;
import com.inclusive.adaptiveeducationservice.featurestore.repository.StudentFeatureRepository;
import com.inclusive.adaptiveeducationservice.mlpipeline.dto.KolbBaselineEvaluationResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class KolbBaselineMlService {

    private final StudentFeatureRepository featureRepository;

    public KolbBaselineMlService(StudentFeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    public KolbBaselineEvaluationResponse evaluateBaseline() {
        var rows = featureRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(StudentFeatureEntity::getFeatureDate))
                .toList();

        var distribution = rows.stream()
                .collect(Collectors.groupingBy(
                        StudentFeatureEntity::getKolbStyle,
                        Collectors.counting()
                ));

        if (rows.isEmpty()) {
            return new KolbBaselineEvaluationResponse(
                    experimentId(), 0, 0, 0, 0,
                    "MAJORITY_CLASS_BASELINE", "NO_DATA",
                    0.0, 0.0, distribution,
                    "NO_FEATURES_AVAILABLE", Instant.now()
            );
        }

        var majorityClass = distribution.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("UNKNOWN");

        var trainEnd = (int) Math.floor(rows.size() * 0.70);
        var validationEnd = (int) Math.floor(rows.size() * 0.85);

        var validationRows = rows.subList(trainEnd, validationEnd);
        var testRows = rows.subList(validationEnd, rows.size());

        return new KolbBaselineEvaluationResponse(
                experimentId(),
                rows.size(),
                trainEnd,
                validationRows.size(),
                testRows.size(),
                "MAJORITY_CLASS_BASELINE",
                majorityClass,
                accuracy(validationRows, majorityClass),
                accuracy(testRows, majorityClass),
                distribution,
                readinessStatus(rows.size(), distribution.size()),
                Instant.now()
        );
    }

    private Double accuracy(List<StudentFeatureEntity> rows, String predictedClass) {
        if (rows.isEmpty()) {
            return 0.0;
        }

        var correct = rows.stream()
                .filter(row -> row.getKolbStyle().equalsIgnoreCase(predictedClass))
                .count();

        return correct / (double) rows.size();
    }

    private String readinessStatus(int totalRows, int classCount) {
        if (totalRows >= 1000 && classCount >= 4) {
            return "READY_FOR_SUPERVISED_ML_EXPERIMENTS";
        }

        if (totalRows >= 100 && classCount >= 3) {
            return "READY_FOR_PILOT_ML_BASELINES";
        }

        return "EARLY_EXPERIMENTAL_BASELINE_ONLY";
    }

    private String experimentId() {
        return "ML-KOLB-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}