package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** Loopback adapter only; all /api traffic runs through the production filter/controller and real H2 services. */
class ProductiveScientificBrowserBridgeTest extends ProductiveScientificPersistenceHttpTest {
    @Test
    void browserUsesProductionBoundary() throws Exception {
        String ready = System.getenv("ILP_PRODUCTIVE_READY");
        Assumptions.assumeTrue(ready != null && !ready.isBlank());
        var json = new ObjectMapper().findAndRegisterModules();
        var released = new CountDownLatch(1);
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            try {
                byte[] bytes;
                int status;
                String path = exchange.getRequestURI().getPath();
                if ("/release".equals(path)) {
                    released.countDown(); status = 200; bytes = new byte[0];
                } else if ("/test/session".equals(path)) {
                    status = 200;
                    bytes = json.writeValueAsBytes(Map.of("token", bearer.substring(7), "tenant", tenant,
                            "assignment", assignment, "evidence", evidence, "submission", protectedRequest));
                } else {
                    var request = MockMvcRequestBuilders.request(HttpMethod.valueOf(exchange.getRequestMethod()), path)
                            .content(exchange.getRequestBody().readAllBytes());
                    exchange.getRequestHeaders().forEach((name, values) -> request.header(name, values.toArray()));
                    var response = protectedMvc.perform(request).andReturn().getResponse();
                    status = response.getStatus(); bytes = response.getContentAsByteArray();
                }
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
                exchange.getResponseBody().write(bytes);
            } catch (Exception exception) {
                byte[] error = "{\"error\":\"TEST_BRIDGE_FAILURE\"}".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, error.length);
                exchange.getResponseBody().write(error);
            } finally { exchange.close(); }
        });
        server.start();
        try {
            Files.writeString(Path.of(ready), "http://127.0.0.1:" + server.getAddress().getPort());
            org.junit.jupiter.api.Assertions.assertTrue(released.await(240, TimeUnit.SECONDS), "Browser did not release fixture");
        } finally { server.stop(0); }
    }
}
