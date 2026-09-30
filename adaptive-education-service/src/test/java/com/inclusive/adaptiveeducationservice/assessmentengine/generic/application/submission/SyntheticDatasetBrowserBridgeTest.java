package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/** Loopback HTTP adapter exists only on the test classpath; persistence is real H2. */
class SyntheticDatasetBrowserBridgeTest extends SyntheticSubmissionHistoryHttpE2ETest {
    @Autowired private GetParticipantAssessmentScientificHistoryService datasetHistory;
    @Autowired private AssessmentResponseService datasetResponses;
    @Autowired private ObjectMapper datasetJson;

    @Test
    @Timeout(180)
    void downloadPersistedSyntheticDatasetInBrowser() throws Exception {
        String readyFile = System.getenv("ILP_DATASET_READY");
        assumeTrue(readyFile != null, "Requires the browser coordinator");
        syntheticAttemptsAreOrderedBySubmissionTimeAndPartitionedByResearchSubject();
        var builder = new SyntheticResearchDatasetBuilder();
        var history = datasetHistory.getByParticipantId("11111111-1111-1111-1111-111111111111");
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
                } else if ("/snapshot".equals(route)) {
                    boolean invalid = "invalid=1".equals(exchange.getRequestURI().getQuery());
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
            assertThat(finished.await(145, TimeUnit.SECONDS)).as("Browser coordinator completed").isTrue();
        } finally {
            server.stop(0);
        }
    }
}
