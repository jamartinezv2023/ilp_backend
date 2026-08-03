package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.query;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.ScientificInterpretation;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.ScientificScoreItem;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.ScientificSubmissionContext;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentScientificResultEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentSubmissionContextEntity;
import org.springframework.stereotype.Component;

@Component
public class AssessmentScientificObservationQueryMapper {

    public AssessmentScientificObservation toModel(
            AssessmentScientificResultEntity result,
            AssessmentSubmissionContextEntity context
    ) {
        var scores = result
                .getScores()
                .stream()
                .map(score ->
                        new ScientificScoreItem(
                                score.getDimensionCode(),
                                score.getNumericValue()
                        )
                )
                .toList();

        var interpretations = result
                .getInterpretations()
                .stream()
                .map(interpretation ->
                        new ScientificInterpretation(
                                interpretation.getInterpretationCode(),
                                interpretation.getInterpretationText(),
                                interpretation.getInterpretationVersion()
                        )
                )
                .toList();

        ScientificSubmissionContext scientificContext =
                new ScientificSubmissionContext(
                        context.getInstitutionId(),
                        context.getCampusId(),
                        context.getProgramId(),
                        context.getCourseId(),
                        context.getCohortId(),
                        context.getTeacherId(),
                        context.getGrade(),
                        context.getAcademicYear(),
                        context.getAcademicPeriod(),
                        context.getFieldworkPhase(),
                        context.getInterventionId(),
                        context.getInterventionGroup(),
                        context.getConsentId(),
                        context.getConsentVersion(),
                        context.getEthicsProtocol(),
                        context.getSource(),
                        context.getDeliveryMode(),
                        context.getLanguage(),
                        context.getDeviceType(),
                        context.getBrowser(),
                        context.getOperatingSystem(),
                        context.getTimezone(),
                        context.getApplicationVersion(),
                        context.getFeatureSetVersion(),
                        context.getPreprocessingVersion(),
                        context.getNormalizationVersion(),
                        context.getStartedAt(),
                        context.getDurationSeconds(),
                        context.getFeatureCutoffAt(),
                        context.getContextJson()
                );

        return new AssessmentScientificObservation(
                result.getAdministrationId(),
                result.getParticipantId(),
                result.getAssessmentCode(),
                result.getAssessmentVersion(),
                result.getPrimaryProfile(),
                result.getScoringAlgorithmVersion(),
                result.getInterpretationVersion(),
                result.getCalculatedAt(),
                result.getSubmittedAt(),
                result.getFeatureCutoffAt(),
                scores,
                interpretations,
                scientificContext
        );
    }
}