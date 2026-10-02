package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inclusive.adaptiveeducationservice.api.assessment.LegacyAssessmentWritePolicy;
import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentRequest;
import com.inclusive.adaptiveeducationservice.api.assessment.KolbAssessmentController;
import com.inclusive.adaptiveeducationservice.api.assessment.SyntheticLoopbackKolbController;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.AssessmentSubmissionExceptionHandler;
import com.inclusive.adaptiveeducationservice.assessment.repository.KolbAssessmentResultRepository;
import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentScientificResultRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentSubmissionContextRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.AssessmentDefinitionRepositoryPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.AssessmentSubmissionValidator;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.AssessmentStrategyRegistry;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb.KolbResultMapper;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb.KolbScoringStrategy;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb.KolbSubmissionMapper;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.student.entity.StudentProfileEntity;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/** Test-only loopback adapter. Real Kolb service, validator, scoring and H2 result repository. */
class SyntheticKolbBrowserBridgeTest extends SyntheticSubmissionHistoryHttpE2ETest {
    @Autowired private ObjectMapper json;
    @Autowired private KolbAssessmentService kolbService;
    @Autowired private KolbAssessmentResultRepository kolbResults;
    @Autowired private StudentProfileRepository students;
    @Autowired private GenericAssessmentEngine engine;
    @Autowired private AssessmentResponseRepository responses;
    @Autowired private AssessmentScientificResultRepository results;
    @Autowired private AssessmentSubmissionContextRepository contexts;
    @MockBean private AssessmentDefinitionRepositoryPort definitions;

    @Test
    void productionLegacyEndpointStillRejectsWrites() throws Exception {
        var service = mock(KolbAssessmentService.class);
        var controller = new KolbAssessmentController(
                service, new LegacyAssessmentWritePolicy()
        );
        var mvc = MockMvcBuilders.standaloneSetup(controller).build();
        List<Integer> ranks = new ArrayList<>();
        for (int group = 0; group < 12; group++) {
            ranks.addAll(List.of(4, 3, 2, 1));
        }
        var request = new KolbAssessmentRequest(
                "SYNTHETIC-STUDENT-001", ranks
        );
        var response = mvc.perform(
                post("/api/v1/assessments/kolb")
                        .contentType("application/json")
                        .content(json.writeValueAsBytes(request))
        ).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(410);
        org.mockito.Mockito.verifyNoInteractions(service);
    }

    @Test
    @Timeout(420)
    void browserUsesRealKolbValidationAndH2() throws Exception {
        String ready = System.getenv("ILP_KOLB_READY");
        assumeTrue(ready != null, "Run with the Kolb browser coordinator");
        kolbResults.deleteAll();
        var student = mock(StudentProfileEntity.class);
        when(students.findById("SYNTHETIC-STUDENT-001"))
                .thenReturn(Optional.of(student));

        List<AssessmentQuestion> questions = new ArrayList<>();
        List<Map<String, Object>> browserQuestions = new ArrayList<>();
        for (int group = 1; group <= 12; group++) {
            String code = "SYNTHETIC-KOLB-Q" + group;
            List<AssessmentOption> options = new ArrayList<>();
            List<Map<String, Object>> browserOptions = new ArrayList<>();
            for (int index = 0; index < 4; index++) {
                String dimension = List.of("CE", "RO", "AC", "AE").get(index);
                String option = code + "-" + dimension;
                options.add(new AssessmentOption(option, option, dimension,
                        dimension, null, 0.0, index + 1));
                browserOptions.add(Map.of("label", dimension,
                        "value", dimension, "displayOrder", index + 1));
            }
            questions.add(new AssessmentQuestion(code, code, "Synthetic group",
                    null, AssessmentQuestionType.IPSATIVE_RANKING,
                    true, group, options));
            browserQuestions.add(Map.of("id", code, "questionNumber", group,
                    "text", "Synthetic group", "options", browserOptions));
        }
        var definition = new AssessmentDefinition("SYNTHETIC-KOLB-DEF", "KOLB_V1",
                "Synthetic ranking fixture", "Not the original inventory",
                "0.0.1-test", "KOLB_BASELINE_V1", null, "Test only", true,
                questions, Instant.now(), Instant.now());
        when(definitions.findLatestActiveByCode("KOLB_V1"))
                .thenReturn(Optional.of(definition));
        var actual = new GenericAssessmentEngine(new AssessmentStrategyRegistry(
                List.of(new KolbScoringStrategy(new KolbAssessmentEngine(),
                        new KolbSubmissionMapper(), new KolbResultMapper()))),
                new AssessmentSubmissionValidator());
        when(engine.evaluate(any(AssessmentDefinition.class),
                any(AssessmentSubmission.class))).thenAnswer(call ->
                actual.evaluate(call.getArgument(0), call.getArgument(1)));
        var mvc = MockMvcBuilders.standaloneSetup(new SyntheticLoopbackKolbController(kolbService))
                .setControllerAdvice(new AssessmentSubmissionExceptionHandler()).build();
        var finished = new CountDownLatch(1);
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            byte[] body;
            int status = 200;
            String type = "application/json; charset=utf-8";
            try {
                String route = exchange.getRequestURI().getPath();
                if ("/release".equals(route)) {
                    finished.countDown();
                    body = "{}".getBytes(StandardCharsets.UTF_8);
                } else if ("/api/v1/assessment-definitions/KOLB_V1".equals(route)) {
                    body = json.writeValueAsBytes(Map.of("questions", browserQuestions));
                } else if ("/test/summary".equals(route)) {
                    Map<String, Object> summary = new LinkedHashMap<>();
                    summary.put("kolb", kolbResults.count());
                    summary.put("responses", responses.count());
                    summary.put("results", results.count());
                    summary.put("contexts", contexts.count());
                    body = json.writeValueAsBytes(summary);
                } else if (route.startsWith("/api/v1/assessments/kolb")) {
                    var request = "POST".equals(exchange.getRequestMethod())
                            ? post(route).contentType("application/json")
                            .content(exchange.getRequestBody().readNBytes(16384))
                            : get(route);
                    var response = mvc.perform(request).andReturn().getResponse();
                    status = response.getStatus();
                    body = response.getContentAsByteArray();
                    if (response.getContentType() != null) {
                        type = response.getContentType();
                    }
                } else {
                    status = 404;
                    body = "{}".getBytes(StandardCharsets.UTF_8);
                }
            } catch (Exception failure) {
                status = 500;
                body = json.writeValueAsBytes(Map.of("code", "TEST_BRIDGE_FAILURE"));
                failure.printStackTrace();
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
            Files.writeString(Path.of(ready), "http://127.0.0.1:"
                    + server.getAddress().getPort());
            assertThat(finished.await(360, TimeUnit.SECONDS)).isTrue();
            assertThat(kolbResults.count()).isEqualTo(2);
            assertThat(responses.count()).isZero();
            assertThat(results.count()).isZero();
            assertThat(contexts.count()).isZero();
        } finally {
            server.stop(0);
        }
    }
}
