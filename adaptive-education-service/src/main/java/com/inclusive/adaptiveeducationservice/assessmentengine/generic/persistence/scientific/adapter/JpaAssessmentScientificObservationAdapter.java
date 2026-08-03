package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.adapter;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.PersistAssessmentScientificObservationCommand;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentScientificResultEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentSubmissionContextEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentScientificResultRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentSubmissionContextRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.AssessmentScientificObservationPort;
import org.springframework.stereotype.Component;

@Component
public class JpaAssessmentScientificObservationAdapter
        implements AssessmentScientificObservationPort {

    private final AssessmentScientificResultRepository
            resultRepository;

    private final AssessmentSubmissionContextRepository
            contextRepository;

    private final AssessmentScientificObservationMapper
            mapper;

    public JpaAssessmentScientificObservationAdapter(
            AssessmentScientificResultRepository resultRepository,
            AssessmentSubmissionContextRepository contextRepository,
            AssessmentScientificObservationMapper mapper
    ) {
        this.resultRepository = resultRepository;
        this.contextRepository = contextRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(
            PersistAssessmentScientificObservationCommand command
    ) {
        String administrationId =
                command.submission()
                        .administrationId();

        if (existsByAdministrationId(administrationId)) {
            throw new IllegalStateException(
                    "Scientific observation already exists for administration: "
                            + administrationId
            );
        }

        AssessmentScientificResultEntity result =
                mapper.toResultEntity(command);

        AssessmentSubmissionContextEntity context =
                mapper.toContextEntity(command);

        resultRepository.save(result);
        contextRepository.save(context);
    }

    @Override
    public boolean existsByAdministrationId(
            String administrationId
    ) {
        return resultRepository
                .existsByAdministrationId(
                        administrationId
                )
                || contextRepository
                .existsByAdministrationId(
                        administrationId
                );
    }
}