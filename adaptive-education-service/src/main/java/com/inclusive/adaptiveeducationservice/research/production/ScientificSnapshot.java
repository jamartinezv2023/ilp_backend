package com.inclusive.adaptiveeducationservice.research.production;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentAnswerResponse;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentResponseResponse;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Map;

public record ScientificSnapshot(String csv, Map<String, Object> manifest) {
    public static ScientificSnapshot create(ScientificAssignment assignment,
            AssessmentResponseResponse response, AssessmentScientificObservation observation) {
        var grant = assignment.grant();
        var csv = new StringBuilder("research_subject_id_json,administration_id_json,instrument_code_json,instrument_version_json,question_id_json,option_id_json,dimension_json,value_json,score\n");
        response.answers().stream().sorted(Comparator.comparing(AssessmentAnswerResponse::questionId)
                .thenComparing(answer -> answer.optionId() == null ? "" : answer.optionId())
                .thenComparing(AssessmentAnswerResponse::id)).forEach(answer -> csv.append(String.join(",",
                        cell(grant.researchSubjectId()), cell(response.id()), cell(response.assessmentCode()),
                        cell(response.assessmentVersion()), cell(answer.questionId()), cell(answer.optionId()),
                        cell(answer.dimension()), cell(answer.value()), cell(answer.score()))).append('\n'));
        String content = csv.toString();
        return new ScientificSnapshot(content, Map.ofEntries(
                Map.entry("schemaVersion", "ILP_AUTHORIZED_ANSWERS_CSV_V1"),
                Map.entry("encoding", "UTF-8"),
                Map.entry("stringEncoding", "Parse JSON in *_json columns; empty cell means null; score is numeric"), Map.entry("sha256", sha256(content)),
                Map.entry("bytes", content.getBytes(StandardCharsets.UTF_8).length),
                Map.entry("rowCount", response.answers().size()), Map.entry("assignmentId", grant.id()),
                Map.entry("institutionId", grant.tenantId()), Map.entry("researchSubjectId", grant.researchSubjectId()),
                Map.entry("administrationId", response.id()), Map.entry("instrumentCode", response.assessmentCode()),
                Map.entry("instrumentVersion", response.assessmentVersion()),
                Map.entry("consentVersion", grant.consentVersion()),
                Map.entry("consentDocumentSha256", assignment.documentSha256()),
                Map.entry("consentEvidenceId", assignment.evidenceId()),
                Map.entry("scores", observation.scores()),
                Map.entry("scoringAlgorithmVersion", observation.scoringAlgorithmVersion()),
                Map.entry("submittedAt", observation.submittedAt()),
                Map.entry("ordering", "question_id,option_id,answer_id; not original questionnaire position")));
    }

    private static String cell(Object value) {
        String text;
        try {
            text = value == null ? "" : new ObjectMapper().writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("CSV value cannot be represented", exception);
        }
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    public static String sha256(String text) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(text.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }
}
