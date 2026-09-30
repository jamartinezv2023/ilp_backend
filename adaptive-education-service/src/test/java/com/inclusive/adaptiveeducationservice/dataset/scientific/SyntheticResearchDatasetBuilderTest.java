package com.inclusive.adaptiveeducationservice.dataset.scientific;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.model.ParticipantAssessmentScientificHistory;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.ScientificSubmissionContext;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentAnswerResponse;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentResponseResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SyntheticResearchDatasetBuilderTest {

    private static final String SUBJECT = "11111111-1111-1111-1111-111111111111";
    private static final Instant TIME = Instant.parse("2026-09-25T16:00:00Z");

    @Test
    void incompleteHistoryIsRejectedBeforeReadingResponses() {
        AtomicInteger reads = new AtomicInteger();
        assertThatThrownBy(() -> new SyntheticResearchDatasetBuilder().build(
                ParticipantAssessmentScientificHistory.empty(SUBJECT), id -> {
                    reads.incrementAndGet();
                    return null;
                })).hasMessage("DATASET_REQUIRES_TWO_COMPLETE_ATTEMPTS");
        assertThat(reads.get()).isZero();
    }

    @ParameterizedTest
    @CsvSource({
            "subject, DATASET_SUBJECT_OR_ATTEMPT_MISMATCH",
            "duplicateAttempt, DATASET_SUBJECT_OR_ATTEMPT_MISMATCH",
            "version, DATASET_VERSION_MISMATCH",
            "scoring, DATASET_VERSION_MISMATCH",
            "instrument, DATASET_VERSION_MISMATCH",
            "time, DATASET_TIME_MISSING",
            "source, DATASET_NON_SYNTHETIC_PROVENANCE",
            "phase, DATASET_NON_SYNTHETIC_PROVENANCE",
            "context, DATASET_NON_SYNTHETIC_PROVENANCE",
            "realAttempt, DATASET_NON_SYNTHETIC_PROVENANCE",
            "language, DATASET_LANGUAGE_MISSING",
            "translation, DATASET_TRANSLATION_VERSION_MISSING",
            "unsafeTranslation, DATASET_UNSAFE_CSV_VALUE",
            "blankInstrument, DATASET_INSTRUMENT_MISSING",
            "blankVersion, DATASET_INSTRUMENT_VERSION_MISSING",
            "blankScoring, DATASET_SCORING_VERSION_MISSING"
    })
    void incompatibleObservationRejectsEntireSnapshot(String defect, String reason) {
        var first = observation("SYNTHETIC-001", TIME);
        var second = observation("SYNTHETIC-002", TIME.plusSeconds(60));
        var context = second.context();
        switch (defect) {
            case "subject" -> when(second.participantId()).thenReturn("OTHER-SUBJECT");
            case "duplicateAttempt" -> when(second.administrationId()).thenReturn("SYNTHETIC-001");
            case "version" -> when(second.assessmentVersion()).thenReturn("2.0");
            case "scoring" -> when(second.scoringAlgorithmVersion()).thenReturn("2.0");
            case "instrument" -> when(second.assessmentCode()).thenReturn("OTHER");
            case "time" -> when(second.submittedAt()).thenReturn(null);
            case "source" -> when(context.source()).thenReturn("FIELDWORK");
            case "phase" -> when(context.fieldworkPhase()).thenReturn("PILOT");
            case "context" -> when(second.context()).thenReturn(null);
            case "realAttempt" -> when(second.administrationId()).thenReturn("REAL-001");
            case "language" -> when(context.language()).thenReturn(" ");
            case "translation" -> when(context.completeContext()).thenReturn(Map.of());
            case "unsafeTranslation" -> when(context.completeContext())
                    .thenReturn(Map.of("translationVersion", "=UNSAFE"));
            case "blankInstrument" -> when(first.assessmentCode()).thenReturn("");
            case "blankVersion" -> when(first.assessmentVersion()).thenReturn("");
            case "blankScoring" -> when(first.scoringAlgorithmVersion()).thenReturn(null);
            default -> throw new IllegalArgumentException(defect);
        }
        assertThatThrownBy(() -> new SyntheticResearchDatasetBuilder().build(
                history(first, second), id -> response(id,
                        id.equals("SYNTHETIC-001") ? TIME : TIME.plusSeconds(60),
                        List.of(answer("Q1", "B")))))
                .isInstanceOf(IllegalArgumentException.class).hasMessage(reason);
    }

    @ParameterizedTest
    @CsvSource({
            "empty, DATASET_ANSWERS_MISSING",
            "null, DATASET_ANSWERS_MISSING",
            "duplicate, DATASET_DUPLICATE_QUESTION",
            "question, DATASET_QUESTION_MISSING",
            "option, DATASET_OPTION_MISSING",
            "unsafe, DATASET_UNSAFE_CSV_VALUE"
    })
    void invalidAnswersCannotProducePartialDataset(String defect, String reason) {
        List<AssessmentAnswerResponse> answers = switch (defect) {
            case "empty" -> List.of();
            case "null" -> null;
            case "duplicate" -> List.of(answer("Q1", "A"), answer("Q1", "B"));
            case "question" -> List.of(answer("", "B"));
            case "option" -> List.of(answer("Q1", null));
            case "unsafe" -> List.of(answer("Q1", "=FORMULA"));
            default -> throw new IllegalArgumentException(defect);
        };
        assertThatThrownBy(() -> new SyntheticResearchDatasetBuilder().build(
                history(observation("SYNTHETIC-001", TIME),
                        observation("SYNTHETIC-002", TIME.plusSeconds(60))),
                id -> response(id, id.equals("SYNTHETIC-001")
                        ? TIME : TIME.plusSeconds(60), answers)))
                .isInstanceOf(IllegalArgumentException.class).hasMessage(reason);
    }

    private static ParticipantAssessmentScientificHistory history(
            AssessmentScientificObservation first, AssessmentScientificObservation second) {
        return new ParticipantAssessmentScientificHistory(SUBJECT, 2,
                TIME, TIME.plusSeconds(60), List.of(first, second));
    }

    private static AssessmentScientificObservation observation(String id, Instant time) {
        var context = mock(ScientificSubmissionContext.class);
        when(context.source()).thenReturn("SYNTHETIC_HTTP_E2E");
        when(context.fieldworkPhase()).thenReturn("TEST_ONLY");
        when(context.language()).thenReturn("es-CO");
        when(context.completeContext()).thenReturn(Map.of("translationVersion", "1.0"));
        var observation = mock(AssessmentScientificObservation.class);
        when(observation.administrationId()).thenReturn(id);
        when(observation.participantId()).thenReturn(SUBJECT);
        when(observation.assessmentCode()).thenReturn("SYNTHETIC-PHYSICS");
        when(observation.assessmentVersion()).thenReturn("1.0");
        when(observation.scoringAlgorithmVersion()).thenReturn("1.0");
        when(observation.submittedAt()).thenReturn(time);
        when(observation.context()).thenReturn(context);
        return observation;
    }

    private static AssessmentResponseResponse response(String id, Instant time,
                                                       List<AssessmentAnswerResponse> answers) {
        return new AssessmentResponseResponse(id, "SYNTHETIC-STUDENT", "SYNTHETIC-PHYSICS",
                "1.0", "COMPLETED", time, answers);
    }

    private static AssessmentAnswerResponse answer(String question, String option) {
        return new AssessmentAnswerResponse("ANSWER", question, option, null, null, null);
    }
}
