package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.adapter;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.PersistAssessmentScientificObservationCommand;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentInterpretationEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentScientificResultEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentScoreItemEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentSubmissionContextEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class AssessmentScientificObservationMapper {

    public AssessmentScientificResultEntity toResultEntity(
            PersistAssessmentScientificObservationCommand command
    ) {
        AssessmentSubmission submission =
                command.submission();

        AssessmentResult result =
                command.result();

        String resultId =
                scientificId(
                        "RESULT",
                        submission.administrationId()
                );

        Instant featureCutoffAt =
                featureCutoffAt(submission);

        AssessmentScientificResultEntity entity =
                new AssessmentScientificResultEntity(
                        resultId,
                        submission.administrationId(),
                        command.researchSubjectId(),
                        submission.assessmentCode(),
                        submission.assessmentVersion(),
                        result.primaryProfile(),
                        result.scoringAlgorithmVersion(),
                        interpretationVersion(result),
                        result.calculatedAt(),
                        submission.submittedAt(),
                        featureCutoffAt
                );

        result.scores()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        entity.addScore(
                                new AssessmentScoreItemEntity(
                                        scientificId(
                                                "SCORE-"
                                                        + normalize(
                                                        entry.getKey()
                                                ),
                                                submission.administrationId()
                                        ),
                                        submission.administrationId(),
                                        entry.getKey(),
                                        entry.getValue()
                                )
                        )
                );

        result.interpretations()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        entity.addInterpretation(
                                new AssessmentInterpretationEntity(
                                        scientificId(
                                                "INTERPRETATION-"
                                                        + normalize(
                                                        entry.getKey()
                                                ),
                                                submission.administrationId()
                                        ),
                                        submission.administrationId(),
                                        entry.getKey(),
                                        entry.getValue(),
                                        interpretationVersion(result)
                                )
                        )
                );

        return entity;
    }

    public AssessmentSubmissionContextEntity toContextEntity(
            PersistAssessmentScientificObservationCommand command
    ) {
        AssessmentSubmission submission =
                command.submission();

        AssessmentScientificContextReader reader =
                new AssessmentScientificContextReader(
                        submission.context()
                );

        Instant featureCutoffAt =
                featureCutoffAt(submission);

        AssessmentSubmissionContextEntity context =
                new AssessmentSubmissionContextEntity(
                        scientificId(
                                "CONTEXT",
                                submission.administrationId()
                        ),
                        submission.administrationId(),
                        featureCutoffAt
                );

        context.defineAcademicContext(
                reader.text("institutionId"),
                reader.text("campusId"),
                reader.text("programId"),
                reader.text("courseId"),
                reader.text("cohortId"),
                reader.text("teacherId"),
                reader.text("grade"),
                reader.text("academicYear"),
                reader.text("academicPeriod")
        );

        context.defineFieldworkContext(
                reader.text("fieldworkPhase"),
                reader.text("interventionId"),
                reader.text("interventionGroup"),
                reader.text("consentId"),
                reader.text("consentVersion"),
                reader.text("ethicsProtocol")
        );

        context.defineTechnicalContext(
                reader.text("source"),
                reader.text("deliveryMode"),
                reader.text("language"),
                reader.text("deviceType"),
                reader.text("browser"),
                reader.text("operatingSystem"),
                reader.text("timezone"),
                reader.text("applicationVersion")
        );

        context.defineFeatureContext(
                reader.text("featureSetVersion"),
                reader.text("preprocessingVersion"),
                reader.text("normalizationVersion")
        );

        context.defineTiming(
                reader.instant("startedAt"),
                reader.longValue("durationSeconds")
        );

        context.replaceContextJson(
                reader.completeContext()
        );

        return context;
    }

    private Instant featureCutoffAt(
            AssessmentSubmission submission
    ) {
        AssessmentScientificContextReader reader =
                new AssessmentScientificContextReader(
                        submission.context()
                );

        Instant explicitCutoff =
                reader.instant("featureCutoffAt");

        return explicitCutoff == null
                ? submission.submittedAt()
                : explicitCutoff;
    }

    private String interpretationVersion(
            AssessmentResult result
    ) {
        String algorithmVersion =
                result.scoringAlgorithmVersion();

        return algorithmVersion == null
                ? "UNVERSIONED"
                : algorithmVersion;
    }

    private String scientificId(
            String prefix,
            String administrationId
    ) {
        return prefix
                + "-"
                + administrationId;
    }

    private String normalize(
            String value
    ) {
        return value
                .trim()
                .toUpperCase()
                .replaceAll(
                        "[^A-Z0-9]+",
                        "-"
                );
    }
}