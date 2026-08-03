package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.query;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentScientificResultRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentSubmissionContextRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.AssessmentScientificObservationQueryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaAssessmentScientificObservationQueryAdapter
        implements AssessmentScientificObservationQueryPort {

    private final AssessmentScientificResultRepository
            resultRepository;

    private final AssessmentSubmissionContextRepository
            contextRepository;

    private final AssessmentScientificObservationQueryMapper
            mapper;

    public JpaAssessmentScientificObservationQueryAdapter(
            AssessmentScientificResultRepository resultRepository,
            AssessmentSubmissionContextRepository contextRepository,
            AssessmentScientificObservationQueryMapper mapper
    ) {
        this.resultRepository = resultRepository;
        this.contextRepository = contextRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<AssessmentScientificObservation>
    findByAdministrationId(
            String administrationId
    ) {
        return resultRepository
                .findByAdministrationId(administrationId)
                .flatMap(result ->
                        contextRepository
                                .findByAdministrationId(
                                        administrationId
                                )
                                .map(context ->
                                        mapper.toModel(
                                                result,
                                                context
                                        )
                                )
                );
    }
}