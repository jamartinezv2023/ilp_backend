package com.inclusive.adaptiveeducationservice.research.production;

import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentAnswerResponse;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentResponseResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.ScientificScoreItem;
import com.inclusive.adaptiveeducationservice.research.application.ScientificApplicationGrant;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class ScientificSnapshotTest {
    @Test
    void snapshotIsDeterministicAndDoesNotExportStudentIdentity() {
        var id = UUID.randomUUID();
        var grant = new ScientificApplicationGrant(id, id, id, "PRIVATE-STUDENT", id,
                "PSEUDONYM", id, "v1", "TEST", "TEST", "v1", true);
        var assignment = new ScientificAssignment(grant, id, id, "abc", id, true, true, true);
        var first = new AssessmentAnswerResponse("A1", "Q1", "OPT1", "D", "=CMD(1)", 4);
        var second = new AssessmentAnswerResponse("A2", "Q2", "OPT2", "D", "3", 3);
        var time = Instant.parse("2026-10-03T12:00:00Z");
        var response = new AssessmentResponseResponse("ADMIN", "PRIVATE-STUDENT", "TEST", "v1", "COMPLETED", time, List.of(second, first));
        var observation = new AssessmentScientificObservation("ADMIN", "PSEUDONYM", "TEST", "v1", "TEST",
                "TEST_SCORE_V1", "TEST_INTERPRETATION", time, time, time,
                List.of(new ScientificScoreItem("D", 7.0)), List.of(), null);
        var snapshot = ScientificSnapshot.create(assignment, response, observation);
        var ordered = new AssessmentResponseResponse("ADMIN", "PRIVATE-STUDENT", "TEST", "v1", "COMPLETED", time, List.of(first, second));
        assertThat(snapshot).isEqualTo(ScientificSnapshot.create(assignment, ordered, observation));
        assertThat(snapshot.csv()).doesNotContain("PRIVATE-STUDENT").contains("=CMD(1)").doesNotContain("'=CMD(1)");
        assertThat(snapshot.manifest()).containsEntry("sha256", ScientificSnapshot.sha256(snapshot.csv()))
                .containsEntry("rowCount", 2).containsEntry("scores", observation.scores());
    }
}
