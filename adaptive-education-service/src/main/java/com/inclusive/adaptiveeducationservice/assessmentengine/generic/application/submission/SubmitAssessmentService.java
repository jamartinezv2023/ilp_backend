package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.AssessmentDefinitionPersistenceMapper;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentresponse.entity.AssessmentAnswerEntity;
import com.inclusive.adaptiveeducationservice.assessmentresponse.entity.AssessmentResponseEntity;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class SubmitAssessmentService {

    private final AssessmentResponseRepository responseRepository;

    private final AssessmentDefinitionRepository definitionRepository;

    private final AssessmentDefinitionPersistenceMapper definitionMapper;

    private final StudentProfileRepository studentProfileRepository;

    private final GenericAssessmentEngine assessmentEngine;

    private final SubmitAssessmentMapper submissionMapper;

    public SubmitAssessmentService(
            AssessmentResponseRepository responseRepository,
            AssessmentDefinitionRepository definitionRepository,
            AssessmentDefinitionPersistenceMapper definitionMapper,
            StudentProfileRepository studentProfileRepository,
            GenericAssessmentEngine assessmentEngine,
            SubmitAssessmentMapper submissionMapper
    ) {
        this.responseRepository = responseRepository;
        this.definitionRepository = definitionRepository;
        this.definitionMapper = definitionMapper;
        this.studentProfileRepository = studentProfileRepository;
        this.assessmentEngine = assessmentEngine;
        this.submissionMapper = submissionMapper;
    }

    @Transactional
    public SubmitAssessmentResponse submit(
            SubmitAssessmentRequest request
    ) {
        validateIdempotency(request.administrationId());
        validateParticipant(request.participantId());

        AssessmentDefinition definition =
                loadDefinition(
                        request.assessmentCode(),
                        request.assessmentVersion()
                );

        AssessmentSubmission submission =
                submissionMapper.toDomain(request);

        AssessmentResult result =
                assessmentEngine.evaluate(
                        definition,
                        submission
                );

        AssessmentResponseEntity persisted =
                persistRawSubmission(
                        definition,
                        submission
                );

        return new SubmitAssessmentResponse(
                result.administrationId(),
                result.participantId(),
                result.assessmentCode(),
                result.assessmentVersion(),
                "COMPLETED",
                result.primaryProfile(),
                result.scores(),
                result.interpretations(),
                result.recommendations(),
                result.scoringAlgorithmVersion(),
                persisted.getAnswers().size(),
                result.calculatedAt()
        );
    }

    private void validateIdempotency(
            String administrationId
    ) {
        if (responseRepository.existsById(administrationId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Assessment submission already exists"
            );
        }
    }

    private void validateParticipant(
            String participantId
    ) {
        if (!studentProfileRepository.existsById(participantId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Student profile not found"
            );
        }
    }

    private AssessmentDefinition loadDefinition(
            String assessmentCode,
            String assessmentVersion
    ) {
        return definitionRepository
                .findByCodeAndActiveTrue(assessmentCode)
                .filter(entity ->
                        assessmentVersion.equals(
                                entity.getVersion()
                        )
                )
                .map(definitionMapper::toDomain)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Active assessment definition and version not found"
                        )
                );
    }

    private AssessmentResponseEntity persistRawSubmission(
            AssessmentDefinition definition,
            AssessmentSubmission submission
    ) {
        AssessmentResponseEntity entity =
                new AssessmentResponseEntity(
                        submission.administrationId(),
                        submission.participantId(),
                        submission.assessmentCode(),
                        submission.assessmentVersion(),
                        "COMPLETED",
                        submission.submittedAt()
                );

        List<AssessmentQuestion> orderedQuestions =
                definition.questions()
                        .stream()
                        .sorted(
                                Comparator.comparing(
                                        AssessmentQuestion::orderIndex
                                )
                        )
                        .toList();

        int answerIndex = 1;

        for (AssessmentQuestion question : orderedQuestions) {
            var response = submission.responses()
                    .stream()
                    .filter(item ->
                            question.code().equals(
                                    item.questionCode()
                            )
                    )
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Validated response is missing question: "
                                            + question.code()
                            )
                    );

            List<AssessmentOption> orderedOptions =
                    question.options()
                            .stream()
                            .sorted(
                                    Comparator.comparing(
                                            AssessmentOption::orderIndex
                                    )
                            )
                            .toList();

            if (!response.rankings().isEmpty()) {
                for (AssessmentOption option : orderedOptions) {
                    Integer ranking =
                            response.rankings()
                                    .get(option.id());

                    if (ranking == null) {
                        ranking =
                                response.rankings()
                                        .get(option.code());
                    }

                    if (ranking == null) {
                        throw new IllegalStateException(
                                "Validated ranking is missing option: "
                                        + option.id()
                        );
                    }

                    entity.addAnswer(
                            createAnswer(
                                    entity.getId(),
                                    answerIndex++,
                                    question,
                                    option,
                                    ranking
                            )
                    );
                }
            } else {
                for (String selectedOptionId :
                        response.selectedOptionIds()) {
                    AssessmentOption option =
                            orderedOptions
                                    .stream()
                                    .filter(item ->
                                            item.id().equals(
                                                    selectedOptionId
                                            )
                                                    || item.code().equals(
                                                    selectedOptionId
                                            )
                                    )
                                    .findFirst()
                                    .orElseThrow(() ->
                                            new IllegalStateException(
                                                    "Validated option not found: "
                                                            + selectedOptionId
                                            )
                                    );

                    entity.addAnswer(
                            createAnswer(
                                    entity.getId(),
                                    answerIndex++,
                                    question,
                                    option,
                                    resolveScore(option)
                            )
                    );
                }
            }
        }

        return responseRepository.save(entity);
    }

    private AssessmentAnswerEntity createAnswer(
            String administrationId,
            int answerIndex,
            AssessmentQuestion question,
            AssessmentOption option,
            int score
    ) {
        return new AssessmentAnswerEntity(
                administrationId
                        + "-A"
                        + String.format(
                                "%03d",
                                answerIndex
                        ),
                question.code(),
                option.id(),
                option.dimension(),
                Integer.toString(score),
                score
        );
    }

    private int resolveScore(
            AssessmentOption option
    ) {
        Double source = option.weight() != null
                ? option.weight()
                : option.numericValue();

        return source == null
                ? 0
                : (int) Math.round(source);
    }
}