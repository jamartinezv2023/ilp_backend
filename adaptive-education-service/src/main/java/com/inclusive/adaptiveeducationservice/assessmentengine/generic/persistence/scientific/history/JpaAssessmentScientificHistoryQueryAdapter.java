package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.history;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentSubmissionContextEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.query.AssessmentScientificObservationQueryMapper;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentScientificResultRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentSubmissionContextRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.AssessmentScientificHistoryQueryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JpaAssessmentScientificHistoryQueryAdapter
        implements AssessmentScientificHistoryQueryPort {

    private final AssessmentScientificResultRepository
            resultRepository;

    private final AssessmentSubmissionContextRepository
            contextRepository;

    private final AssessmentScientificObservationQueryMapper
            observationMapper;

    public JpaAssessmentScientificHistoryQueryAdapter(
            AssessmentScientificResultRepository resultRepository,
            AssessmentSubmissionContextRepository contextRepository,
            AssessmentScientificObservationQueryMapper observationMapper
    ) {
        this.resultRepository = resultRepository;
        this.contextRepository = contextRepository;
        this.observationMapper = observationMapper;
    }

    @Override
    public List<AssessmentScientificObservation>
    findByParticipantId(
            String participantId
    ) {
        var results =
                resultRepository
                        .findByParticipantIdOrderBySubmittedAtDescAdministrationIdAsc(
                                participantId
                        );

        if (results.isEmpty()) {
            return List.of();
        }

        List<String> administrationIds =
                results.stream()
                        .map(result ->
                                result.getAdministrationId()
                        )
                        .toList();

        Map<String, AssessmentSubmissionContextEntity>
                contextByAdministrationId =
                contextRepository
                        .findByAdministrationIdIn(
                                administrationIds
                        )
                        .stream()
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        AssessmentSubmissionContextEntity
                                                ::getAdministrationId,
                                        Function.identity()
                                )
                        );

        return results.stream()
                .map(result -> {
                    AssessmentSubmissionContextEntity context =
                            contextByAdministrationId.get(
                                    result.getAdministrationId()
                            );

                    if (context == null) {
                        throw new IllegalStateException(
                                "Scientific context missing for administration: "
                                        + result.getAdministrationId()
                        );
                    }

                    return observationMapper.toModel(
                            result,
                            context
                    );
                })
                .toList();
    }
}