package com.inclusive.adaptiveeducationservice.research.production;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.GetParticipantAssessmentScientificHistoryService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission.SubmitAssessmentService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.assessmentresponse.service.AssessmentResponseService;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ConsentRecordRepository;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ResearchParticipantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ScientificProductionConfigurationTest {
    @Test
    void shouldRequireAuthorizationSchemaBeforeBuildingService() {
        var database = new EmbeddedDatabaseBuilder().generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2).build();
        try {
            var jdbc = new JdbcTemplate(database);
            var configuration = new ScientificProductionConfiguration();
            assertThatThrownBy(() -> configuration.scientificAssignments(jdbc))
                    .isInstanceOf(org.springframework.dao.DataAccessException.class);
            jdbc.execute("CREATE TABLE scientific_assignments (id integer)");
            var assignments = configuration.scientificAssignments(jdbc);
            var participants = mock(ResearchParticipantRepository.class);
            var consents = mock(ConsentRecordRepository.class);
            var identities = mock(ScientificParticipantIdentityPort.class);
            var submissions = mock(SubmitAssessmentService.class);
            var histories = mock(GetParticipantAssessmentScientificHistoryService.class);
            var responses = mock(AssessmentResponseService.class);
            assertThat(configuration.scientificProductionService(assignments, participants,
                    consents, identities, submissions, histories, responses)).isNotNull();
        } finally {
            database.shutdown();
        }
    }

    @Test
    void shouldLoadRsaPublicKeyAndRegisterApiBoundary() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var encoded = Base64.getMimeEncoder().encodeToString(generator.generateKeyPair().getPublic().getEncoded());
        var pem = "-----BEGIN PUBLIC KEY-----\n" + encoded + "\n-----END PUBLIC KEY-----\n";
        var resource = new ByteArrayResource(pem.getBytes(StandardCharsets.US_ASCII));
        var configuration = new ScientificProductionConfiguration();
        var verifier = configuration.scientificTokenVerifier(resource, "urn:ilp:test", "ilp-scientific-api");
        var registration = configuration.scientificApiBoundary(verifier);
        assertThat(registration.getOrder()).isEqualTo(1);
        assertThat(registration.getUrlPatterns()).containsExactly("/api/*");
        var request = new MockHttpServletRequest("GET", "/api/v1/scientific-applications/history");
        var response = new MockHttpServletResponse();
        registration.getFilter().doFilter(request, response, new MockFilterChain());
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void shouldRejectInvalidPublicKey() {
        var configuration = new ScientificProductionConfiguration();
        var resource = new ByteArrayResource("invalid-key".getBytes(StandardCharsets.US_ASCII));
        assertThatThrownBy(() -> configuration.scientificTokenVerifier(resource,
                "urn:ilp:test", "ilp-scientific-api")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldAllowOnlyConfiguredCorsOriginAndHeaders() throws Exception {
        var configuration = new ScientificProductionConfiguration();
        var registration = configuration.scientificCors("https://research.example.org");
        assertThat(registration.getOrder()).isZero();
        assertThat(registration.getUrlPatterns()).containsExactly("/api/*");
        for (var origin : List.of("https://research.example.org", "https://other.example.org")) {
            var request = new MockHttpServletRequest("OPTIONS", "/api/v1/scientific-applications/history");
            request.addHeader("Origin", origin);
            request.addHeader("Access-Control-Request-Method", "GET");
            request.addHeader("Access-Control-Request-Headers", "Authorization,X-Tenant-Id,X-Scientific-Grant");
            var response = new MockHttpServletResponse();
            registration.getFilter().doFilter(request, response, new MockFilterChain());
            if (origin.equals("https://research.example.org")) {
                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getHeader("Access-Control-Allow-Origin")).isEqualTo(origin);
                assertThat(response.getHeader("Access-Control-Allow-Credentials")).isNull();
            } else {
                assertThat(response.getStatus()).isEqualTo(403);
                assertThat(response.getHeader("Access-Control-Allow-Origin")).isNull();
            }
        }
        for (var origin : List.of("http://research.example.org", "https://*.example.org",
                "https://one.example.org,https://two.example.org")) {
            assertThatThrownBy(() -> configuration.scientificCors(origin))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
