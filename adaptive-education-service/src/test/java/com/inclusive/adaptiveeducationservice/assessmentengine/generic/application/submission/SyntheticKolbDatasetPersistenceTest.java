package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.inclusive.adaptiveeducationservice.assessment.entity.KolbAssessmentResultEntity;
import com.inclusive.adaptiveeducationservice.assessment.repository.KolbAssessmentResultRepository;
import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentEngine;
import com.inclusive.adaptiveeducationservice.dataset.scientific.SyntheticKolbDatasetBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Reloads from a new persistence context; an in-memory submit object cannot satisfy this test. */
class SyntheticKolbDatasetPersistenceTest extends SyntheticSubmissionHistoryHttpE2ETest {
    @Autowired private KolbAssessmentResultRepository kolbResults;
    @Autowired private PlatformTransactionManager transactions;

    @Test
    void h2RetainsRankPositionsForSnapshotAfterTransactionEnds() {
        List<Integer> ranks = new ArrayList<>();
        var patterns = List.of(List.of(4, 3, 2, 1), List.of(1, 4, 3, 2), List.of(3, 1, 4, 2));
        for (int index = 0; index < 48; index++) {
            ranks.add(patterns.get(index / 4 % 3).get(index % 4));
        }
        var scores = new KolbAssessmentEngine().calculate(ranks);
        var row = new KolbAssessmentResultEntity("KOLB-ABCDEF12", "SYNTHETIC-STUDENT-001",
                scores.scoreCE(), scores.scoreRO(), scores.scoreAC(), scores.scoreAE(),
                scores.learningStyle(), "KOLB_BASELINE_V1",
                Instant.parse("2026-10-02T05:00:00.123456Z"), ranks);
        var transaction = new TransactionTemplate(transactions);
        transaction.executeWithoutResult(status -> kolbResults.save(row));
        var loaded = transaction.execute(status -> kolbResults.findById(row.getId()).orElseThrow());
        assertThat(loaded).isNotSameAs(row);
        assertThat(loaded.getAnswers()).containsExactlyElementsOf(ranks);
        var snapshot = new SyntheticKolbDatasetBuilder().build(loaded);
        assertThat(snapshot.manifest().assessmentId()).isEqualTo(row.getId());
        assertThat(snapshot.manifest().createdAt()).isEqualTo(row.getCreatedAt());
        transaction.executeWithoutResult(status -> kolbResults.deleteById(row.getId()));
    }
}
