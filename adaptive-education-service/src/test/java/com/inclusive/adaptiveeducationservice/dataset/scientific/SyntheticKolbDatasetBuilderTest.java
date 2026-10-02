package com.inclusive.adaptiveeducationservice.dataset.scientific;

import com.inclusive.adaptiveeducationservice.assessment.entity.KolbAssessmentResultEntity;
import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentEngine;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SyntheticKolbDatasetBuilderTest {
    private final SyntheticKolbDatasetBuilder builder = new SyntheticKolbDatasetBuilder();

    @Test
    void snapshotKeepsEveryPositionAndHashesExactUtf8Bytes() throws Exception {
        var ranks = ranks();
        var row = row("KOLB-ABCDEF12", "SYNTHETIC-STUDENT-001", "KOLB_BASELINE_V1",
                Instant.parse("2026-10-02T05:00:00.123456Z"), ranks);
        var snapshot = builder.build(row);
        assertThat(snapshot.manifest().csvSha256()).isEqualTo(HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256")
                        .digest(snapshot.csv().getBytes(StandardCharsets.UTF_8))));
        assertThat(snapshot.manifest().columns()).hasSize(58);
        assertThat(snapshot.manifest().answers()).isEqualTo(48);
        assertThat(snapshot.manifest().records()).isEqualTo(1);
        assertThat(snapshot.manifest().dataClass()).isEqualTo("SYNTHETIC_ONLY");
        assertThat(snapshot.csv()).endsWith("\n").doesNotContain("\r");
        var values = snapshot.csv().split("\n")[1].split(",");
        for (int index = 0; index < 48; index++) {
            assertThat(values[index + 10]).isEqualTo("\"" + ranks.get(index) + "\"");
        }
        assertThat(builder.build(row)).isEqualTo(snapshot);
    }

    @Test
    void rejectsInvalidLineageWithoutProducingSnapshot() {
        var time = Instant.parse("2026-10-02T05:00:00Z");
        for (var row : List.of(
                row(null, "SYNTHETIC-STUDENT-001", "KOLB_BASELINE_V1", time, ranks()),
                row("KOLB-ABCDEF12", "REAL-STUDENT", "KOLB_BASELINE_V1", time, ranks()),
                row("KOLB-ABCDEF12", "SYNTHETIC-STUDENT-001", "OTHER", time, ranks()),
                row("KOLB-ABCDEF12", "SYNTHETIC-STUDENT-001", "KOLB_BASELINE_V1", null, ranks()),
                row("INVALID", "SYNTHETIC-STUDENT-001", "KOLB_BASELINE_V1", time, ranks()))) {
            assertThatThrownBy(() -> builder.build(row))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("KOLB_DATASET_LINEAGE_INVALID");
        }
        assertThatThrownBy(() -> builder.build(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsRepeatedRanksAndInconsistentScores() {
        var ranks = ranks();
        var invalid = new ArrayList<>(ranks);
        invalid.set(1, invalid.get(0));
        var scores = new KolbAssessmentEngine().calculate(ranks);
        var repeated = new KolbAssessmentResultEntity("KOLB-ABCDEF12", "SYNTHETIC-STUDENT-001",
                scores.scoreCE(), scores.scoreRO(), scores.scoreAC(), scores.scoreAE(),
                scores.learningStyle(), "KOLB_BASELINE_V1", Instant.now(), invalid);
        assertThatThrownBy(() -> builder.build(repeated))
                .isInstanceOf(IllegalArgumentException.class);
        for (int field = 0; field < 5; field++) {
            var wrongScores = new KolbAssessmentResultEntity("KOLB-ABCDEF12",
                    "SYNTHETIC-STUDENT-001", scores.scoreCE() + (field == 0 ? 1 : 0),
                    scores.scoreRO() + (field == 1 ? 1 : 0),
                    scores.scoreAC() + (field == 2 ? 1 : 0),
                    scores.scoreAE() + (field == 3 ? 1 : 0),
                    field == 4 ? "INCORRECT" : scores.learningStyle(),
                    "KOLB_BASELINE_V1", Instant.now(), ranks);
            assertThatThrownBy(() -> builder.build(wrongScores))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("KOLB_DATASET_SCORE_MISMATCH");
        }
    }

    private List<Integer> ranks() {
        List<Integer> result = new ArrayList<>();
        var patterns = List.of(List.of(4, 3, 2, 1), List.of(1, 4, 3, 2), List.of(3, 1, 4, 2));
        for (int index = 0; index < 48; index++) {
            result.add(patterns.get(index / 4 % 3).get(index % 4));
        }
        return result;
    }

    private KolbAssessmentResultEntity row(String id, String student, String version,
                                           Instant time, List<Integer> answers) {
        var scores = new KolbAssessmentEngine().calculate(answers);
        return new KolbAssessmentResultEntity(id, student, scores.scoreCE(),
                scores.scoreRO(), scores.scoreAC(), scores.scoreAE(), scores.learningStyle(),
                version, time, answers);
    }
}
