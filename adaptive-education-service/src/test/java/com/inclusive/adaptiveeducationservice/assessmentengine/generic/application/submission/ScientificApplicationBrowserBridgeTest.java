package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inclusive.adaptiveeducationservice.api.assessmentresponse.AssessmentResponseController;
import com.inclusive.adaptiveeducationservice.api.assessmentscientifichistory.ParticipantAssessmentScientificHistoryController;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.AssessmentSubmissionController;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.AssessmentSubmissionExceptionHandler;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentDefinitionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.GetParticipantAssessmentScientificHistoryService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.AssessmentDefinitionPersistenceMapper;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentScientificResultRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentSubmissionContextRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.AssessmentSubmissionValidator;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.AssessmentStrategyRegistry;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb.KolbResultMapper;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb.KolbScoringStrategy;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb.KolbSubmissionMapper;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.assessmentresponse.service.AssessmentResponseService;
import com.inclusive.adaptiveeducationservice.dataset.scientific.SyntheticResearchDatasetBuilder;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/** Canonical generic persistence with real mapping, ranking validation and scoring.
 * Identity, consent and definition are explicit synthetic fixtures, not field validation.
 */
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:ilp_scientific_application;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
})
class ScientificApplicationBrowserBridgeTest extends SyntheticSubmissionHistoryHttpE2ETest {
    private static final String SUBJECT = "11111111-1111-1111-1111-111111111111";
    private static final UUID IDENTITY = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    @Autowired private SubmitAssessmentService submissions;
    @Autowired private SubmitAssessmentMapper mapper;
    @Autowired private GenericAssessmentEngine engine;
    @Autowired private AssessmentDefinitionRepository definitions;
    @Autowired private AssessmentDefinitionPersistenceMapper definitionMapper;
    @Autowired private ScientificParticipantIdentityPort identities;
    @Autowired private AssessmentResponseRepository responses;
    @Autowired private AssessmentScientificResultRepository results;
    @Autowired private AssessmentSubmissionContextRepository contexts;
    @Autowired private GetParticipantAssessmentScientificHistoryService history;
    @Autowired private AssessmentResponseService raw;
    @Autowired private ObjectMapper json;
    private MockMvc mvc;
    private List<Map<String, Object>> browserQuestions;
    private final AtomicBoolean consent = new AtomicBoolean(true);

    void prepareScientificApplication() {
        consent.set(true);
        var questions = new ArrayList<AssessmentQuestion>();
        browserQuestions = new ArrayList<>();
        for (int group = 1; group <= 12; group++) {
            String code = "SYNTHETIC-KOLB-Q" + group;
            var options = new ArrayList<AssessmentOption>();
            var browserOptions = new ArrayList<Map<String, String>>();
            for (int index = 0; index < 4; index++) {
                String dimension = List.of("CE", "RO", "AC", "AE").get(index);
                String id = code + "-" + dimension;
                options.add(new AssessmentOption(id, id, dimension, dimension, null, 0.0, index + 1));
                browserOptions.add(Map.of("id", id, "dimension", dimension));
            }
            questions.add(new AssessmentQuestion(code, code, "Synthetic group", null,
                    AssessmentQuestionType.IPSATIVE_RANKING, true, group, options));
            browserQuestions.add(Map.of("code", code, "options", browserOptions));
        }
        var definition = new AssessmentDefinition("SYNTHETIC-DEF", "KOLB_V1", "Synthetic fixture",
                "Not an official inventory", "0.0.1-test", "KOLB_BASELINE_V1", null,
                "Test only", true, questions, Instant.now(), Instant.now());
        var entity = mock(AssessmentDefinitionEntity.class);
        when(entity.getVersion()).thenReturn("0.0.1-test");
        when(definitions.findByCodeAndActiveTrue("KOLB_V1")).thenReturn(Optional.of(entity));
        when(definitionMapper.toDomain(entity)).thenReturn(definition);
        var actualMapper = new SubmitAssessmentMapper();
        when(mapper.toDomain(any(SubmitAssessmentRequest.class))).thenAnswer(call ->
                actualMapper.toDomain(call.getArgument(0)));
        var actualEngine = new GenericAssessmentEngine(new AssessmentStrategyRegistry(List.of(
                new KolbScoringStrategy(new KolbAssessmentEngine(), new KolbSubmissionMapper(),
                        new KolbResultMapper()))), new AssessmentSubmissionValidator());
        when(engine.evaluate(any(AssessmentDefinition.class), any(AssessmentSubmission.class)))
                .thenAnswer(call -> actualEngine.evaluate(call.getArgument(0), call.getArgument(1)));
        when(identities.hasActiveResearchConsent(IDENTITY)).thenAnswer(call -> consent.get());
        mvc = MockMvcBuilders.standaloneSetup(new AssessmentSubmissionController(submissions),
                new AssessmentResponseController(raw),
                new ParticipantAssessmentScientificHistoryController(history))
                .setControllerAdvice(new AssessmentSubmissionExceptionHandler()).build();
    }

    @Test
    @Timeout(420)
    void browserUsesCanonicalScientificPersistence() throws Exception {
        String ready = System.getenv("ILP_SCIENTIFIC_READY");
        assumeTrue(ready != null, "Requires scientific browser coordinator");
        prepareScientificApplication();
        var done = new CountDownLatch(1);
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            int status = 200;
            byte[] body;
            String route = exchange.getRequestURI().getPath();
            try {
                if ("/release".equals(route)) {
                    done.countDown(); body = json.writeValueAsBytes(Map.of());
                } else if ("/test/session".equals(route)) {
                    body = json.writeValueAsBytes(session());
                } else if ("/test/consent".equals(route)) {
                    consent.set(!"active=false".equals(exchange.getRequestURI().getQuery()));
                    body = json.writeValueAsBytes(Map.of("active", consent.get()));
                } else if ("/test/summary".equals(route)) {
                    body = json.writeValueAsBytes(Map.of("responses", responses.count(),
                            "results", results.count(), "contexts", contexts.count()));
                } else if (route.startsWith("/test/snapshot/")) {
                    body = json.writeValueAsBytes(snapshot(route.substring("/test/snapshot/".length())));
                } else if (route.startsWith("/api/")) {
                    var request = "POST".equals(exchange.getRequestMethod())
                            ? post(route).contentType("application/json")
                                    .content(exchange.getRequestBody().readAllBytes()) : get(route);
                    var result = mvc.perform(request).andReturn().getResponse();
                    status = result.getStatus(); body = result.getContentAsByteArray();
                    if (body.length == 0) {
                        body = json.writeValueAsBytes(Map.of("status", status));
                    }
                } else {
                    status = 404; body = json.writeValueAsBytes(Map.of());
                }
            } catch (Exception exception) {
                status = 422; body = json.writeValueAsBytes(Map.of("code", "SCIENTIFIC_LAB_REJECTED"));
            }
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.sendResponseHeaders(status, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        try {
            server.start();
            Files.writeString(Path.of(ready), "http://127.0.0.1:" + server.getAddress().getPort());
            assertThat(done.await(360, TimeUnit.SECONDS)).isTrue();
            assertThat(responses.count()).isEqualTo(2);
            assertThat(results.count()).isEqualTo(2);
            assertThat(contexts.count()).isEqualTo(2);
        } finally {
            server.stop(0);
        }
    }

    @Test
    void canonicalHttpGuardsPreventWritesAndAcceptedAttemptSurvivesReload() throws Exception {
        prepareScientificApplication();
        var payload = session();
        payload.remove("questions");
        payload.remove("researchSubjectId");
        payload.remove("translationVersion");
        payload.remove("consentId");
        payload.remove("consentVersion");
        payload.put("submittedAt", "2026-10-02T10:00:10Z");
        var timing = new LinkedHashMap<String, String>();
        timing.put("source", "SYNTHETIC_SCIENTIFIC_APPLICATION");
        timing.put("fieldworkPhase", "TEST_ONLY");
        timing.put("language", "es");
        timing.put("translationVersion", "synthetic-labels-v1");
        timing.put("consentId", "SYNTHETIC-CONSENT-001");
        timing.put("consentVersion", "test-consent-v1");
        timing.put("startedAt", "2026-10-02T10:00:00Z");
        timing.put("durationSeconds", "11");
        payload.put("context", timing);
        var answers = new ArrayList<Map<String, Object>>();
        for (int group = 1; group <= 12; group++) {
            String code = "SYNTHETIC-KOLB-Q" + group;
            answers.add(Map.of("questionCode", code, "rankings", Map.of(
                    code + "-CE", 4, code + "-RO", 3, code + "-AC", 2, code + "-AE", 1)));
        }
        payload.put("responses", answers);
        assertThat(mvc.perform(post("/api/v1/assessment-submissions").contentType("application/json")
                .content(json.writeValueAsBytes(payload))).andReturn().getResponse().getStatus()).isEqualTo(400);
        timing.put("durationSeconds", "10");
        consent.set(false);
        assertThat(mvc.perform(post("/api/v1/assessment-submissions").contentType("application/json")
                .content(json.writeValueAsBytes(payload))).andReturn().getResponse().getStatus()).isEqualTo(403);
        assertThat(responses.count()).isZero();
        assertThat(results.count()).isZero();
        assertThat(contexts.count()).isZero();
        consent.set(true);
        assertThat(mvc.perform(post("/api/v1/assessment-submissions").contentType("application/json")
                .content(json.writeValueAsBytes(payload))).andReturn().getResponse().getStatus()).isEqualTo(201);
        assertThat(mvc.perform(post("/api/v1/assessment-submissions").contentType("application/json")
                .content(json.writeValueAsBytes(payload))).andReturn().getResponse().getStatus()).isEqualTo(409);
        assertThat(responses.count()).isEqualTo(1);
        assertThat(results.count()).isEqualTo(1);
        assertThat(contexts.count()).isEqualTo(1);
        var id = payload.get("administrationId").toString();
        var saved = contexts.findByAdministrationId(id).orElseThrow();
        assertThat(saved.getStartedAt()).isEqualTo(Instant.parse("2026-10-02T10:00:00Z"));
        assertThat(saved.getDurationSeconds()).isEqualTo(10L);
        var exported = snapshot(id);
        assertThat(exported.get("csv").toString().split("\n")).hasSize(49);
        assertThat(snapshot(id)).isEqualTo(exported);
    }

    private Map<String, Object> session() {
        var session = new LinkedHashMap<String, Object>();
        session.put("administrationId", "SYNTHETIC-ADMIN-" + UUID.randomUUID());
        session.put("participantId", "SYNTHETIC-STUDENT-001");
        session.put("researchParticipantUuid", IDENTITY);
        session.put("researchSubjectId", SUBJECT);
        session.put("assessmentCode", "KOLB_V1");
        session.put("assessmentVersion", "0.0.1-test");
        session.put("translationVersion", "synthetic-labels-v1");
        session.put("consentId", "SYNTHETIC-CONSENT-001");
        session.put("consentVersion", "test-consent-v1");
        session.put("questions", browserQuestions);
        return session;
    }

    private Map<String, String> variable(String name) {
        String description = switch (name) {
            case "research_subject_id" -> "Resolved synthetic research identity; excludes operational student ID";
            case "administration_id" -> "Unique application identifier; reused when retrying the same request";
            case "instrument_code" -> "Registered scoring route; does not certify an official inventory";
            case "instrument_version" -> "Persisted synthetic definition version";
            case "scoring_algorithm_version" -> "Persisted scoring implementation version";
            case "language" -> "Language selected at the start of this application";
            case "translation_version" -> "Synthetic label version; not a validated translation";
            case "consent_id" -> "Client-provided synthetic consent reference; eligibility is a test fixture";
            case "consent_version" -> "Client-provided synthetic consent document version";
            case "started_at" -> "Client-reported application start, ISO-8601 UTC";
            case "submitted_at" -> "Client-reported submission timestamp persisted by the service, ISO-8601 UTC";
            case "duration_seconds" -> "Floor of submitted minus started seconds; not verified active attention";
            case "answer_id" -> "Persisted answer identifier with positional suffix";
            case "question_id" -> "Persisted synthetic question code";
            case "option_id" -> "Persisted synthetic option identifier";
            case "dimension" -> "Persisted CE, RO, AC or AE option dimension";
            case "rank" -> "Persisted integer rank 1 through 4, unique within each synthetic group";
            case "dimension_score" -> "Persisted dimension total repeated per option; do not sum repeated totals";
            default -> throw new IllegalArgumentException("Unknown dictionary variable");
        };
        return Map.of("name", name, "description", description, "type",
                List.of("rank", "dimension_score", "duration_seconds").contains(name) ? "number" : "string",
                "missing", "NOT_ALLOWED", "source", "PERSISTED_GENERIC_READ_MODELS");
    }

    private Map<String, Object> snapshot(String id) throws Exception {
        var observation = history.getByParticipantId(SUBJECT).observations().stream()
                .filter(item -> item.administrationId().equals(id)).findFirst().orElseThrow();
        var response = raw.findById(id);
        var context = observation.context();
        if (!id.startsWith("SYNTHETIC-ADMIN-") || !SUBJECT.equals(observation.participantId())
                || !"SYNTHETIC-STUDENT-001".equals(response.studentId())
                || !"KOLB_V1".equals(observation.assessmentCode())
                || !"0.0.1-test".equals(observation.assessmentVersion())
                || !observation.assessmentVersion().equals(response.assessmentVersion())
                || !observation.submittedAt().equals(response.submittedAt())
                || !"SYNTHETIC_SCIENTIFIC_APPLICATION".equals(context.source())
                || !"TEST_ONLY".equals(context.fieldworkPhase())
                || response.answers().size() != 48 || context.startedAt() == null
                || context.durationSeconds() == null) {
            throw new IllegalArgumentException("SCIENTIFIC_LINEAGE_INVALID");
        }
        var columns = List.of("research_subject_id", "administration_id", "instrument_code",
                "instrument_version", "scoring_algorithm_version", "language", "translation_version",
                "consent_id", "consent_version", "started_at", "submitted_at", "duration_seconds",
                "answer_id", "question_id", "option_id", "dimension", "rank", "dimension_score");
        String dictionary = json.writer().with(com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValueAsString(Map.of("schemaVersion", "scientific-application-v1",
                "dataClass", "SYNTHETIC_ONLY", "timingSource", "CLIENT_REPORTED",
                "rowUnit", "ONE_RANKED_OPTION", "instrumentValidation", "NOT_VALIDATED",
                "variables", columns.stream().map(this::variable).toList())) + "\n";
        var scores = new LinkedHashMap<String, Double>();
        observation.scores().forEach(item -> scores.put(item.dimensionCode(), item.numericValue()));
        var ordered = response.answers().stream().sorted(Comparator.comparing(
                com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentAnswerResponse::id)).toList();
        for (int group = 0; group < 12; group++) {
            var ranks = ordered.subList(group * 4, group * 4 + 4).stream()
                    .map(item -> item.score()).sorted().toList();
            if (!ranks.equals(List.of(1, 2, 3, 4))) {
                throw new IllegalArgumentException("SCIENTIFIC_RANKS_INVALID");
            }
        }
        for (String dimension : List.of("CE", "RO", "AC", "AE")) {
            double sum = ordered.stream().filter(item -> dimension.equals(item.dimension()))
                    .mapToInt(item -> item.score()).sum();
            if (!Double.valueOf(sum).equals(scores.get(dimension))) {
                throw new IllegalArgumentException("SCIENTIFIC_SCORE_MISMATCH");
            }
        }
        var csv = new StringBuilder(String.join(",", columns)).append('\n');
        for (var answer : ordered) {
            List<String> cells = List.of(SUBJECT, id, observation.assessmentCode(),
                    observation.assessmentVersion(), observation.scoringAlgorithmVersion(), context.language(),
                    context.completeContext().get("translationVersion").toString(), context.consentId(),
                    context.consentVersion(), context.startedAt().toString(), observation.submittedAt().toString(),
                    context.durationSeconds().toString(), answer.id(), answer.questionId(), answer.optionId(),
                    answer.dimension(), answer.score().toString(), scores.get(answer.dimension()).toString());
            csv.append(cells.stream().map(value -> "\"" + value.replace("\"", "\"\"") + "\"")
                    .collect(java.util.stream.Collectors.joining(","))).append('\n');
        }
        var manifest = new LinkedHashMap<String, Object>();
        manifest.put("schemaVersion", "scientific-application-v1");
        manifest.put("dataClass", "SYNTHETIC_ONLY");
        manifest.put("administrationId", id); manifest.put("participantId", SUBJECT);
        manifest.put("assessmentVersion", observation.assessmentVersion());
        manifest.put("scoringAlgorithmVersion", observation.scoringAlgorithmVersion());
        manifest.put("csvSha256", SyntheticResearchDatasetBuilder.sha256(csv.toString()));
        manifest.put("dictionarySha256", SyntheticResearchDatasetBuilder.sha256(dictionary));
        manifest.put("rows", ordered.size()); manifest.put("columns", columns);
        manifest.put("encoding", "UTF-8"); manifest.put("lineEnding", "LF");
        return Map.of("csv", csv.toString(), "dictionary", dictionary, "manifest", manifest);
    }
}
