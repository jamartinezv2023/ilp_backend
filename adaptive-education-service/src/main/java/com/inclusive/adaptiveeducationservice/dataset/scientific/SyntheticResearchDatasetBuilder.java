package com.inclusive.adaptiveeducationservice.dataset.scientific;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.model.ParticipantAssessmentScientificHistory;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentAnswerResponse;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentResponseResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Internal, synthetic-only snapshot assembly. It has deliberately no HTTP
 * controller or access to a production database. This is not a field export.
 */
public final class SyntheticResearchDatasetBuilder {

    public record Manifest(String schemaVersion, String instrumentCode,
                           String instrumentVersion, String scoringAlgorithmVersion,
                           Instant firstSubmittedAt, Instant lastSubmittedAt,
                           int acceptedAttempts, int excludedAttempts,
                           int answerRows, String csvSha256, String dataClass) {
    }

    public record Snapshot(String csv, Manifest manifest) {
    }

    public Snapshot build(ParticipantAssessmentScientificHistory history,
                          Function<String, AssessmentResponseResponse> responseLookup) {
        Objects.requireNonNull(history, "history");
        Objects.requireNonNull(responseLookup, "responseLookup");
        if (history.totalObservations() < 2
                || history.observations().size() != history.totalObservations()) {
            throw new IllegalArgumentException("DATASET_REQUIRES_TWO_COMPLETE_ATTEMPTS");
        }
        if (history.participantId() == null || !history.participantId().matches(
                "[0-9a-fA-F]{8}(-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}")) {
            throw new IllegalArgumentException("DATASET_SUBJECT_FORMAT_INVALID");
        }
        if (history.observations().stream().anyMatch(item -> item.submittedAt() == null)) {
            throw new IllegalArgumentException("DATASET_TIME_MISSING");
        }

        List<AssessmentScientificObservation> observations = history.observations()
                .stream()
                .sorted(Comparator.comparing(AssessmentScientificObservation::submittedAt)
                        .thenComparing(AssessmentScientificObservation::administrationId))
                .toList();
        var first = observations.get(0);
        requireText(first.assessmentCode(), "DATASET_INSTRUMENT_MISSING");
        requireText(first.assessmentVersion(), "DATASET_INSTRUMENT_VERSION_MISSING");
        requireText(first.scoringAlgorithmVersion(), "DATASET_SCORING_VERSION_MISSING");
        requireSafeCell(first.assessmentCode());
        requireSafeCell(first.assessmentVersion());
        requireSafeCell(first.scoringAlgorithmVersion());

        StringBuilder csv = new StringBuilder("research_subject_id,administration_id,instrument_code,"
                + "instrument_version,submitted_at,scoring_algorithm_version,question_id,option_id,"
                + "language,translation_version,source,fieldwork_phase\n");
        HashSet<String> attemptIds = new HashSet<>();
        int rows = 0;
        for (var observation : observations) {
            if (!Objects.equals(history.participantId(), observation.participantId())
                    || !attemptIds.add(observation.administrationId())) {
                throw new IllegalArgumentException("DATASET_SUBJECT_OR_ATTEMPT_MISMATCH");
            }
            if (!Objects.equals(first.assessmentCode(), observation.assessmentCode())
                    || !Objects.equals(first.assessmentVersion(), observation.assessmentVersion())
                    || !Objects.equals(first.scoringAlgorithmVersion(), observation.scoringAlgorithmVersion())) {
                throw new IllegalArgumentException("DATASET_VERSION_MISMATCH");
            }
            if (!observation.administrationId().startsWith("SYNTHETIC-")
                    || observation.context() == null
                    || !"SYNTHETIC_HTTP_E2E".equals(observation.context().source())
                    || !"TEST_ONLY".equals(observation.context().fieldworkPhase())) {
                throw new IllegalArgumentException("DATASET_NON_SYNTHETIC_PROVENANCE");
            }
            requireSafeCell(observation.administrationId());
            requireText(observation.context().language(), "DATASET_LANGUAGE_MISSING");
            requireSafeCell(observation.context().language());
            Object translation = observation.context().completeContext().get("translationVersion");
            if (!(translation instanceof String translationVersion)
                    || translationVersion.isBlank()) {
                throw new IllegalArgumentException("DATASET_TRANSLATION_VERSION_MISSING");
            }
            requireSafeCell(translationVersion);

            AssessmentResponseResponse response = Objects.requireNonNull(
                    responseLookup.apply(observation.administrationId()), "response");
            if (!observation.administrationId().equals(response.id())
                    || response.studentId() == null
                    || !response.studentId().startsWith("SYNTHETIC-")
                    || !observation.assessmentCode().equals(response.assessmentCode())
                    || !observation.assessmentVersion().equals(response.assessmentVersion())
                    || !observation.submittedAt().equals(response.submittedAt())) {
                throw new IllegalArgumentException("DATASET_RESPONSE_LINEAGE_MISMATCH");
            }
            if (response.answers() == null || response.answers().isEmpty()) {
                throw new IllegalArgumentException("DATASET_ANSWERS_MISSING");
            }
            HashSet<String> questions = new HashSet<>();
            for (AssessmentAnswerResponse answer : response.answers().stream()
                    .sorted(Comparator.comparing(AssessmentAnswerResponse::questionId))
                    .toList()) {
                requireText(answer.questionId(), "DATASET_QUESTION_MISSING");
                requireText(answer.optionId(), "DATASET_OPTION_MISSING");
                requireSafeCell(answer.questionId());
                requireSafeCell(answer.optionId());
                if (!questions.add(answer.questionId())) {
                    throw new IllegalArgumentException("DATASET_DUPLICATE_QUESTION");
                }
                appendRow(csv, history.participantId(), observation.administrationId(),
                        observation.assessmentCode(), observation.assessmentVersion(),
                        observation.submittedAt().toString(), observation.scoringAlgorithmVersion(),
                        answer.questionId(), answer.optionId(), observation.context().language(),
                        translationVersion, observation.context().source(),
                        observation.context().fieldworkPhase());
                rows++;
            }
        }
        String data = csv.toString();
        return new Snapshot(data, new Manifest("1.0-synthetic", first.assessmentCode(),
                first.assessmentVersion(), first.scoringAlgorithmVersion(),
                first.submittedAt(), observations.get(observations.size() - 1).submittedAt(),
                observations.size(), 0, rows, sha256(data), "SYNTHETIC_ONLY"));
    }

    private static void requireText(String value, String code) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(code);
        }
    }

    private static void requireSafeCell(String value) {
        if (!value.matches("[A-Za-z0-9._-]+")) {
            throw new IllegalArgumentException("DATASET_UNSAFE_CSV_VALUE");
        }
    }

    private static void appendRow(StringBuilder csv, String... cells) {
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) {
                csv.append(',');
            }
            csv.append('"').append(cells[i].replace("\"", "\"\"")).append('"');
        }
        csv.append('\n');
    }

    public static String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }
}
