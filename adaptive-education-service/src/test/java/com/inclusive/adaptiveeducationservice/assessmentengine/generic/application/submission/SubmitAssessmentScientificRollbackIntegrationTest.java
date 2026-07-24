package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentQuestionRequest;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentDefinitionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.AssessmentDefinitionPersistenceMapper;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentScientificResultRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentSubmissionContextRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.AssessmentScientificObservationPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest(
        properties = {
                "spring.flyway.enabled=false",
                "spring.sql.init.mode=never",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.open-in-view=false"
        }
)
@ActiveProfiles("local")
class SubmitAssessmentScientificRollbackIntegrationTest {

    private static final String ADMINISTRATION_ID =
            "ADMIN-ROLLBACK-001";

    private static final String PARTICIPANT_ID =
            "ST-ROLLBACK-001";

    private static final Instant SUBMITTED_AT =
            Instant.parse("2026-07-23T18:00:00Z");

    @Autowired
    private SubmitAssessmentService service;

    @Autowired
    private AssessmentResponseRepository responseRepository;

    @Autowired
    private AssessmentScientificResultRepository resultRepository;

    @Autowired
    private AssessmentSubmissionContextRepository contextRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private AssessmentDefinitionRepository definitionRepository;

    @MockBean
    private AssessmentDefinitionPersistenceMapper definitionMapper;

    @MockBean
    private StudentProfileRepository studentProfileRepository;

    @MockBean
    private GenericAssessmentEngine assessmentEngine;

    @MockBean
    private SubmitAssessmentMapper submissionMapper;

    @MockBean
    private AssessmentScientificObservationPort
            scientificObservationPort;

    private SubmitAssessmentRequest request;
    private AssessmentSubmission submission;
    private AssessmentResult result;
    private AssessmentDefinition definition;

    @BeforeEach
    void setUp() {
        reset(
                definitionRepository,
                definitionMapper,
                studentProfileRepository,
                assessmentEngine,
                submissionMapper,
                scientificObservationPort
        );

        contextRepository.deleteAll();
        resultRepository.deleteAll();
        responseRepository.deleteAll();

        Integer orphanAnswerCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM assessment_answers
                        """,
                        Integer.class
                );

        assertThat(orphanAnswerCount)
                .isZero();

        SubmitAssessmentQuestionRequest questionRequest =
                new SubmitAssessmentQuestionRequest(
                        "Q1",
                        List.of(),
                        Map.of(
                                "Q1-CE", 4,
                                "Q1-RO", 3,
                                "Q1-AC", 2,
                                "Q1-AE", 1
                        ),
                        null,
                        null
                );

        request =
                new SubmitAssessmentRequest(
                        ADMINISTRATION_ID,
                        PARTICIPANT_ID,
                        "KOLB_V1",
                        "1.0",
                        List.of(questionRequest),
                        Map.of(
                                "source",
                                "ROLLBACK_INTEGRATION_TEST"
                        ),
                        SUBMITTED_AT
                );

        AssessmentResponse domainResponse =
                new AssessmentResponse(
                        "Q1",
                        List.of(),
                        Map.of(
                                "Q1-CE", 4,
                                "Q1-RO", 3,
                                "Q1-AC", 2,
                                "Q1-AE", 1
                        ),
                        null,
                        null
                );

        submission =
                new AssessmentSubmission(
                        ADMINISTRATION_ID,
                        PARTICIPANT_ID,
                        "KOLB_V1",
                        "1.0",
                        List.of(domainResponse),
                        Map.of(
                                "source",
                                "ROLLBACK_INTEGRATION_TEST"
                        ),
                        SUBMITTED_AT
                );

        result =
                new AssessmentResult(
                        ADMINISTRATION_ID,
                        PARTICIPANT_ID,
                        "KOLB_V1",
                        "1.0",
                        "DIVERGENT",
                        Map.of(
                                "CE", 4.0,
                                "RO", 3.0,
                                "AC", 2.0,
                                "AE", 1.0,
                                "AC_MINUS_CE", -2.0,
                                "AE_MINUS_RO", -2.0
                        ),
                        Map.of(
                                "PRIMARY_PROFILE",
                                "Perfil divergente"
                        ),
                        List.of(),
                        "KOLB_BASELINE_V1",
                        SUBMITTED_AT.plusSeconds(1)
                );

        List<AssessmentOption> options =
                List.of(
                        new AssessmentOption(
                                "Q1-CE",
                                "Q1-CE",
                                "Experiencia concreta",
                                "CE",
                                null,
                                1.0,
                                1
                        ),
                        new AssessmentOption(
                                "Q1-RO",
                                "Q1-RO",
                                "Observación reflexiva",
                                "RO",
                                null,
                                1.0,
                                2
                        ),
                        new AssessmentOption(
                                "Q1-AC",
                                "Q1-AC",
                                "Conceptualización abstracta",
                                "AC",
                                null,
                                1.0,
                                3
                        ),
                        new AssessmentOption(
                                "Q1-AE",
                                "Q1-AE",
                                "Experimentación activa",
                                "AE",
                                null,
                                1.0,
                                4
                        )
                );

        AssessmentQuestion question =
                new AssessmentQuestion(
                        "Q1",
                        "Q1",
                        "Ordene las alternativas",
                        null,
                        AssessmentQuestionType.IPSATIVE_RANKING,
                        true,
                        1,
                        options
                );

        definition =
                org.mockito.Mockito.mock(
                        AssessmentDefinition.class
                );

        AssessmentDefinitionEntity definitionEntity =
                org.mockito.Mockito.mock(
                        AssessmentDefinitionEntity.class
                );

        when(
                scientificObservationPort
                        .existsByAdministrationId(
                                ADMINISTRATION_ID
                        )
        ).thenReturn(false);

        when(
                studentProfileRepository.existsById(
                        PARTICIPANT_ID
                )
        ).thenReturn(true);

        when(
                definitionRepository
                        .findByCodeAndActiveTrue(
                                "KOLB_V1"
                        )
        ).thenReturn(
                Optional.of(definitionEntity)
        );

        when(definitionEntity.getVersion())
                .thenReturn("1.0");

        when(
                definitionMapper.toDomain(
                        definitionEntity
                )
        ).thenReturn(definition);

        when(definition.questions())
                .thenReturn(
                        List.of(question)
                );

        when(submissionMapper.toDomain(request))
                .thenReturn(submission);

        when(
                assessmentEngine.evaluate(
                        definition,
                        submission
                )
        ).thenReturn(result);
    }
    @Test
    void shouldRollbackRawResponseAndAnswersWhenScientificPersistenceFails() {
        doAnswer(invocation -> {
            entityManager.flush();

            Integer responseCount =
                    jdbcTemplate.queryForObject(
                            """
                            SELECT COUNT(*)
                            FROM assessment_responses
                            WHERE id = ?
                            """,
                            Integer.class,
                            ADMINISTRATION_ID
                    );

            Integer answerCount =
                    jdbcTemplate.queryForObject(
                            """
                            SELECT COUNT(*)
                            FROM assessment_answers
                            WHERE assessment_response_id = ?
                            """,
                            Integer.class,
                            ADMINISTRATION_ID
                    );

            assertThat(responseCount)
                    .isEqualTo(1);

            assertThat(answerCount)
                    .isEqualTo(4);

            throw new IllegalStateException(
                    "Forced scientific persistence failure"
            );
        })
                .when(scientificObservationPort)
                .save(any());

        assertThatThrownBy(() ->
                service.submit(request)
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessageContaining(
                        "Forced scientific persistence failure"
                );

        assertThat(
                responseRepository.existsById(
                        ADMINISTRATION_ID
                )
        ).isFalse();

        Integer answerCountAfterRollback =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM assessment_answers
                        WHERE assessment_response_id = ?
                        """,
                        Integer.class,
                        ADMINISTRATION_ID
                );

        assertThat(answerCountAfterRollback)
                .isZero();

        assertThat(
                resultRepository.existsByAdministrationId(
                        ADMINISTRATION_ID
                )
        ).isFalse();

        assertThat(
                contextRepository.existsByAdministrationId(
                        ADMINISTRATION_ID
                )
        ).isFalse();
    }
    @Test
    void shouldCommitRawResponseWhenScientificPersistenceSucceeds() {
        doAnswer(invocation -> {
            entityManager.flush();
            return null;
        })
                .when(scientificObservationPort)
                .save(any());

        service.submit(request);

        assertThat(
                responseRepository.existsById(
                        ADMINISTRATION_ID
                )
        ).isTrue();

        Integer answerCountAfterCommit =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM assessment_answers
                        WHERE assessment_response_id = ?
                        """,
                        Integer.class,
                        ADMINISTRATION_ID
                );

        assertThat(answerCountAfterCommit)
                .isEqualTo(4);
    }}