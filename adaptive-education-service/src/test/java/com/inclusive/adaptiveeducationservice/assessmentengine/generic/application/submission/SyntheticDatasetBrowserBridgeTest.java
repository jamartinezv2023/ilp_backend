package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentQuestionRequest;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.AssessmentDefinitionPersistenceMapper;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.GetParticipantAssessmentScientificHistoryService;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentResponseResponse;
import com.inclusive.adaptiveeducationservice.assessmentresponse.service.AssessmentResponseService;
import com.inclusive.adaptiveeducationservice.dataset.scientific.SyntheticResearchDatasetBuilder;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

/** Loopback HTTP adapter exists only on the test classpath; persistence is real H2. */
class SyntheticDatasetBrowserBridgeTest extends SyntheticSubmissionHistoryHttpE2ETest {
    @Autowired private GetParticipantAssessmentScientificHistoryService datasetHistory;
    @Autowired private AssessmentResponseService datasetResponses;
    @Autowired private ObjectMapper datasetJson;
    @Autowired private SubmitAssessmentService labSubmit;
    @Autowired private SubmitAssessmentMapper labMapper;
    @Autowired private AssessmentDefinitionRepository labDefinitions;
    @Autowired private AssessmentDefinitionPersistenceMapper labDefinitionsMapper;
    @Autowired private GenericAssessmentEngine labEngine;
    @Autowired private ScientificParticipantIdentityPort labIdentity;

    private void configureInteractiveFixtures() {
        var definition = labDefinitionsMapper.toDomain(labDefinitions
                .findByCodeAndActiveTrue("ILP-SYNTHETIC-PHYSICS").orElseThrow());
        var a = new AssessmentOption("PHYSICS-Q1-A", "PHYSICS-Q1-A", "A", "SYNTHETIC_SCORE", null, 1.0, 1);
        var b = new AssessmentOption("PHYSICS-Q1-B", "PHYSICS-Q1-B", "B", "SYNTHETIC_SCORE", null, 1.0, 2);
        when(definition.questions()).thenReturn(List.of(new AssessmentQuestion(
                "PHYSICS-Q1", "PHYSICS-Q1", "Synthetic question", null,
                AssessmentQuestionType.SINGLE_CHOICE, true, 1, List.of(a, b))));
        when(labMapper.toDomain(any(SubmitAssessmentRequest.class))).thenAnswer(call -> {
            SubmitAssessmentRequest incoming = call.getArgument(0);
            return new AssessmentSubmission(incoming.administrationId(), incoming.participantId(),
                    incoming.assessmentCode(), incoming.assessmentVersion(),
                    incoming.responses().stream().map(answer -> new AssessmentResponse(
                            answer.questionCode(), answer.selectedOptionIds(), Map.of(), null, null)).toList(),
                    incoming.context(), incoming.submittedAt());
        });
        when(labEngine.evaluate(any(), any())).thenAnswer(call -> {
            AssessmentSubmission incoming = call.getArgument(1);
            return new AssessmentResult(incoming.administrationId(), incoming.participantId(),
                    incoming.assessmentCode(), incoming.assessmentVersion(), "SYNTHETIC_FEEDBACK",
                    Map.of("SYNTHETIC_SCORE", 1.0), Map.of("FEEDBACK", "Test only"),
                    List.of(), "SYNTHETIC_SCORING_TEST", incoming.submittedAt());
        });
    }

    @Test
    @Timeout(720)
    void downloadPersistedSyntheticDatasetInBrowser() throws Exception {
        String readyFile = System.getenv("ILP_DATASET_READY");
        assumeTrue(readyFile != null, "Requires the browser coordinator");
        configureInteractiveFixtures();
        var builder = new SyntheticResearchDatasetBuilder();

        var finished = new CountDownLatch(1);
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            byte[] body;
            String type = "application/json; charset=utf-8";
            int status = 200;
            String route = exchange.getRequestURI().getPath();
            try {
                if ("/release".equals(route)) {
                    body = "{}".getBytes(StandardCharsets.UTF_8);
                    finished.countDown();
                } else if ("/submit".equals(route)) {
                    if (!"POST".equals(exchange.getRequestMethod())) {
                        throw new IllegalArgumentException("POST required");
                    }
                    var input = datasetJson.readTree(exchange.getRequestBody().readNBytes(4097));
                    String option = input.path("option").asText();
                    String language = input.path("language").asText();
                    UUID session = UUID.fromString(input.path("session").asText());
                    if (input.size() != 3 || !List.of("PHYSICS-Q1-A", "PHYSICS-Q1-B").contains(option)
                            || !List.of("es", "en").contains(language)) {
                        throw new IllegalArgumentException("Invalid synthetic input");
                    }
                    when(labIdentity.hasActiveResearchConsent(session)).thenReturn(true);
                    when(labIdentity.resolveResearchSubjectId(session)).thenReturn(Optional.of(session.toString()));
                    var incoming = new SubmitAssessmentRequest("SYNTHETIC-" + UUID.randomUUID(),
                            "SYNTHETIC-STUDENT-001", session, "ILP-SYNTHETIC-PHYSICS", "0.0.1-test",
                            List.of(new SubmitAssessmentQuestionRequest("PHYSICS-Q1", List.of(option),
                                    Map.of(), null, null)),
                            Map.of("source", "SYNTHETIC_HTTP_E2E", "language", language.equals("es") ? "es-CO" : "en-US",
                                    "translationVersion", "0.0.1-test", "fieldworkPhase", "TEST_ONLY"), Instant.now());
                    body = datasetJson.writeValueAsBytes(labSubmit.submit(incoming));
                    status = 201;
                } else if ("/snapshot".equals(route)) {
                    var parameters = exchange.getRequestURI().getQuery();
                    var subject = UUID.fromString(parameters.split("&")[0].replace("session=", "")).toString();
                    var history = datasetHistory.getByParticipantId(subject);
                    boolean invalid = parameters.endsWith("&invalid=1");
                    var snapshot = builder.build(history, id -> {
                        var response = datasetResponses.findById(id);
                        if (!invalid) {
                            return response;
                        }
                        return new AssessmentResponseResponse(response.id(), response.studentId(),
                                response.assessmentCode(), "OTHER-VERSION", response.status(),
                                response.submittedAt(), response.answers());
                    });
                    body = datasetJson.writeValueAsBytes(snapshot);
                } else if ("/lineage".equals(route)) {
                    var subject = UUID.fromString(exchange.getRequestURI().getQuery().replace("session=", "")).toString();
                    var history = datasetHistory.getByParticipantId(subject);
                    body = datasetJson.writeValueAsBytes(Map.of("history", history,
                            "responses", history.observations().stream()
                                    .map(item -> datasetResponses.findById(item.administrationId())).toList()));
                } else if ("/".equals(route) || "/app.mjs".equals(route)) {
                    String name = "/".equals(route) ? "index.html" : "app.mjs";
                    type = name.endsWith("html") ? "text/html; charset=utf-8" : "text/javascript; charset=utf-8";
                    body = Files.readAllBytes(Path.of("..", "e2e", "dataset-browser", name));
                } else {
                    status = 404;
                    body = "{}".getBytes(StandardCharsets.UTF_8);
                }
            } catch (IllegalArgumentException invalid) {
                status = 422;
                body = datasetJson.writeValueAsBytes(Map.of("code", "DATASET_VALIDATION_FAILED"));
            }
            exchange.getResponseHeaders().set("Content-Type", type);
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.sendResponseHeaders(status, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        try {
            server.start();
            Files.writeString(Path.of(readyFile), "http://127.0.0.1:" + server.getAddress().getPort());
            long sessionSeconds = "true".equals(System.getenv("ILP_DATASET_MANUAL")) ? 600 : 145;
            assertThat(finished.await(sessionSeconds, TimeUnit.SECONDS)).as("Browser coordinator completed").isTrue();
        } finally {
            server.stop(0);
        }
    }
}
