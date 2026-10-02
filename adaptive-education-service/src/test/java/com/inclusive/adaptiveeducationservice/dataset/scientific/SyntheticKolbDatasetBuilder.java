package com.inclusive.adaptiveeducationservice.dataset.scientific;

import com.inclusive.adaptiveeducationservice.assessment.entity.KolbAssessmentResultEntity;
import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentEngine;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

/** Internal synthetic snapshot builder. No Spring bean or production HTTP endpoint. */
public final class SyntheticKolbDatasetBuilder {
    public record Manifest(String schemaVersion, String dataClass, String instrumentCode,
                           String storedVersion, String assessmentId, String participantId,
                           Instant createdAt, int records, int answers, String encoding,
                           String lineEnding, String csvSha256, List<String> columns) {
    }

    public record Snapshot(String csv, Manifest manifest) {
    }

    public Snapshot build(KolbAssessmentResultEntity row) {
        Objects.requireNonNull(row, "row");
        if (!"SYNTHETIC-STUDENT-001".equals(row.getStudentId())
                || row.getId() == null || !row.getId().matches("KOLB-[A-F0-9]{8}")
                || !"KOLB_BASELINE_V1".equals(row.getInstrumentVersion())
                || row.getCreatedAt() == null) {
            throw new IllegalArgumentException("KOLB_DATASET_LINEAGE_INVALID");
        }
        var scores = new KolbAssessmentEngine().calculate(row.getAnswers());
        if (!Objects.equals(scores.scoreCE(), row.getScoreCE())
                || !Objects.equals(scores.scoreRO(), row.getScoreRO())
                || !Objects.equals(scores.scoreAC(), row.getScoreAC())
                || !Objects.equals(scores.scoreAE(), row.getScoreAE())
                || !Objects.equals(scores.learningStyle(), row.getLearningStyle())) {
            throw new IllegalArgumentException("KOLB_DATASET_SCORE_MISMATCH");
        }
        List<String> columns = new ArrayList<>(List.of("assessment_id", "participant_id",
                "instrument_code", "stored_version", "created_at", "score_ce", "score_ro",
                "score_ac", "score_ae", "learning_style"));
        List<String> values = new ArrayList<>(List.of(row.getId(), row.getStudentId(),
                "KOLB_V1", row.getInstrumentVersion(), row.getCreatedAt().toString(),
                row.getScoreCE().toString(), row.getScoreRO().toString(),
                row.getScoreAC().toString(), row.getScoreAE().toString(), row.getLearningStyle()));
        for (int index = 0; index < row.getAnswers().size(); index++) {
            columns.add(String.format(java.util.Locale.ROOT, "answer_%02d", index + 1));
            values.add(row.getAnswers().get(index).toString());
        }
        String csv = String.join(",", columns) + "\n"
                + String.join(",", values.stream().map(this::cell).toList()) + "\n";
        return new Snapshot(csv, new Manifest("kolb-synthetic-wide-v1", "SYNTHETIC_ONLY",
                "KOLB_V1", row.getInstrumentVersion(), row.getId(), row.getStudentId(),
                row.getCreatedAt(), 1, 48, "UTF-8", "LF", sha256(csv), List.copyOf(columns)));
    }

    private String cell(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String sha256(String csv) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(csv.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException failure) {
            throw new IllegalStateException("SHA-256 unavailable", failure);
        }
    }
}
