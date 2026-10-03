package com.inclusive.adaptiveeducationservice.research.production;

import com.inclusive.adaptiveeducationservice.api.scientific.ScientificProductionController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentQuestionRequest;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.GetParticipantAssessmentScientificHistoryService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission.SubmitAssessmentService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.assessmentresponse.service.AssessmentResponseService;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.ConsentRecord;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.ResearchParticipant;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ConsentRecordRepository;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ResearchParticipantRepository;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Real RSA verification, production filter/controller and JDBC registry; scoring port is mocked. */
class ScientificProductionHttpTest {
    private final UUID tenant = UUID.randomUUID();
    private final UUID user = UUID.randomUUID();
    private final UUID assignment = UUID.randomUUID();
    private final UUID evidence = UUID.randomUUID();
    private final UUID document = UUID.randomUUID();
    private JdbcTemplate jdbc;
    private KeyPair keys;
    private MockMvc mvc;
    private SubmitAssessmentService submissions;
    private ConsentRecordRepository consents;
    private ConsentRecord consent;
    private SubmitAssessmentRequest request;
    private String token;

    protected DriverManagerDataSource createDataSource() {
        return new DriverManagerDataSource("jdbc:h2:mem:" + UUID.randomUUID()
                + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "");
    }

    @BeforeEach
    void prepare() throws Exception {
        var source = createDataSource();
        new ResourceDatabasePopulator(new ClassPathResource(
                "db/scientific-production/V1__scientific_authorization_registry.sql")).execute(source);
        jdbc = new JdbcTemplate(source);
        var participants = mock(ResearchParticipantRepository.class);
        consents = mock(ConsentRecordRepository.class);
        var identities = mock(ScientificParticipantIdentityPort.class);
        submissions = mock(SubmitAssessmentService.class);
        var participant = new ResearchParticipant("SYNTHETIC-PRODUCTIVE-001", "APPROVED", "TEST");
        consent = new ConsentRecord(participant.getParticipantCode(), "SYNTHETIC_RESEARCH", "APPROVED");
        when(participants.findById(participant.getParticipantUuid())).thenReturn(Optional.of(participant));
        when(consents.findById(consent.getConsentId())).thenReturn(Optional.of(consent));
        when(consents.findFirstByParticipantCodeOrderByCreatedAtDesc(participant.getParticipantCode()))
                .thenReturn(Optional.of(consent));
        when(identities.hasActiveResearchConsent(participant.getParticipantUuid())).thenReturn(true);
        when(identities.resolveResearchSubjectId(participant.getParticipantUuid())).thenReturn(Optional.of("SUBJECT-001"));
        jdbc.update("INSERT INTO scientific_memberships VALUES (?, ?, TRUE, ?, CURRENT_TIMESTAMP)", tenant, user, user);
        jdbc.update("INSERT INTO scientific_participant_bindings VALUES (?, ?, 'STUDENT-001', 'SUBJECT-001', TRUE, ?, CURRENT_TIMESTAMP)",
                tenant, participant.getParticipantUuid(), user);
        jdbc.update("INSERT INTO scientific_consent_documents VALUES (?, ?, 'v1', 'Synthetic consent', ?, ?, CURRENT_TIMESTAMP, TRUE)",
                document, tenant, ScientificSnapshot.sha256("Synthetic consent"), user);
        jdbc.update("""
                INSERT INTO scientific_consent_evidence VALUES (?, ?, ?, ?, ?, 'SYNTHETIC_RESEARCH', ?, ?,
                    'ADULT_SELF', 'TEST_ONLY', CURRENT_TIMESTAMP, NULL, NULL)
                """, evidence, tenant, participant.getParticipantUuid(), document, consent.getConsentId(), user, ScientificSnapshot.sha256("Synthetic consent"));
        jdbc.update("""
                INSERT INTO scientific_instrument_permissions VALUES (?, 'TEST', 'v1', TRUE,
                    'SYNTHETIC_FIXTURE_NOT_VALIDATED_INSTRUMENT', ?, CURRENT_TIMESTAMP)
                """, tenant, user);
        jdbc.update("""
                INSERT INTO scientific_assignments VALUES (?, ?, ?, ?, ?, 'TEST', 'v1',
                    TRUE, TRUE, TRUE, TRUE, ?, ?, CURRENT_TIMESTAMP)
                """, assignment, tenant, user, participant.getParticipantUuid(), evidence,
                java.sql.Timestamp.from(Instant.now().plusSeconds(3600)), user);
        var service = new ScientificProductionService(new ScientificAssignmentStore(jdbc), participants,
                consents, identities, submissions, mock(GetParticipantAssessmentScientificHistoryService.class),
                mock(AssessmentResponseService.class));
        var proxy = new ProxyFactory(service);
        proxy.setProxyTargetClass(true);
        proxy.addAdvice(new TransactionInterceptor(new DataSourceTransactionManager(source),
                new AnnotationTransactionAttributeSource()));
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keys = generator.generateKeyPair();
        var verifier = new ScientificTokenVerifier((RSAPublicKey) keys.getPublic(), "urn:ilp:test", "ilp-scientific-api");
        mvc = MockMvcBuilders.standaloneSetup(new ScientificProductionController(
                (ScientificProductionService) proxy.getProxy())).addFilters(new ScientificApiFilter(verifier)).build();
        request = new SubmitAssessmentRequest("ADMIN-001", "STUDENT-001", participant.getParticipantUuid(),
                "TEST", "v1", List.of(new SubmitAssessmentQuestionRequest("Q1", List.of("A1"), Map.of(), null, null)),
                Map.of("consentId", consent.getConsentId().toString(), "consentVersion", "v1",
                        "institutionId", "FORGED", "authorizedUserId", "FORGED"), Instant.now());
        when(submissions.submit(any())).thenReturn(mock(SubmitAssessmentResponse.class));
        token = signedToken(user, tenant, "ilp-scientific-api", Instant.now().plusSeconds(300));
    }

    private String signedToken(UUID actor, UUID institution, String audience, Instant expiry) throws Exception {
        var claims = new JWTClaimsSet.Builder().issuer("urn:ilp:test").audience(audience)
                .subject(actor.toString()).claim("tenantId", institution.toString())
                .issueTime(Date.from(Instant.now().minusSeconds(5))).expirationTime(Date.from(expiry)).build();
        var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
        jwt.sign(new RSASSASigner(keys.getPrivate()));
        return "Bearer " + jwt.serialize();
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder submission(String bearer)
            throws Exception {
        return post("/api/v1/assessment-submissions").header("Authorization", bearer)
                .header("X-Tenant-Id", tenant).header("X-Scientific-Grant", assignment)
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().findAndRegisterModules().writeValueAsBytes(request));
    }

    @Test
    void authorizedSubmissionUsesServerOwnedProvenance() throws Exception {
        mvc.perform(submission(token)).andExpect(status().isCreated());
        var capture = org.mockito.ArgumentCaptor.forClass(SubmitAssessmentRequest.class);
        verify(submissions).submit(capture.capture());
        assertThat(capture.getValue().context()).containsEntry("authorizedUserId", user.toString())
                .containsEntry("institutionId", tenant.toString())
                .containsEntry("consentDocumentSha256", ScientificSnapshot.sha256("Synthetic consent"));
        assertThat(request.context()).containsEntry("institutionId", "FORGED");
    }

    @Test
    void rejectsWrongUserAndTenantWithoutSubmitting() throws Exception {
        mvc.perform(submission(signedToken(UUID.randomUUID(), tenant, "ilp-scientific-api",
                Instant.now().plusSeconds(300)))).andExpect(status().isForbidden());
        mvc.perform(submission(signedToken(user, UUID.randomUUID(), "ilp-scientific-api",
                Instant.now().plusSeconds(300)))).andExpect(status().isForbidden());
        verifyNoInteractions(submissions);
    }

    @Test
    void rejectsMissingExpiredAndWrongAudienceTokens() throws Exception {
        mvc.perform(submission("invalid")).andExpect(status().isUnauthorized());
        mvc.perform(submission(signedToken(user, tenant, "wrong", Instant.now().plusSeconds(300))))
                .andExpect(status().isUnauthorized());
        mvc.perform(submission(signedToken(user, tenant, "ilp-scientific-api", Instant.now().minusSeconds(120))))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(submissions);
    }

    @ParameterizedTest
    @ValueSource(strings = {"UPDATE scientific_memberships SET active=FALSE",
            "UPDATE scientific_participant_bindings SET active=FALSE",
            "UPDATE scientific_assignments SET active=FALSE",
            "UPDATE scientific_assignments SET can_submit=FALSE",
            "UPDATE scientific_assignments SET valid_until=assigned_at",
            "UPDATE scientific_instrument_permissions SET approved=FALSE",
            "UPDATE scientific_consent_documents SET active=FALSE",
            "UPDATE scientific_consent_documents SET document_text='CORRUPTED'"})
    void rejectsRevokedRegistryState(String sql) throws Exception {
        if (sql.contains("valid_until")) {
            jdbc.update("UPDATE scientific_assignments SET assigned_at=?, valid_until=?",
                    java.sql.Timestamp.from(Instant.now().minusSeconds(300)),
                    java.sql.Timestamp.from(Instant.now().minusSeconds(120)));
        } else { jdbc.update(sql); }
        mvc.perform(submission(token)).andExpect(status().isForbidden());
        verifyNoInteractions(submissions);
    }

    @Test
    void withdrawalSurvivesExpiredAssignmentAndPreventsFutureOperations() throws Exception {
        jdbc.update("UPDATE scientific_assignments SET active=FALSE");
        mvc.perform(post("/api/v1/scientific-applications/consents/{id}/withdraw", evidence)
                .header("Authorization", token).header("X-Tenant-Id", tenant)).andExpect(status().isNoContent());
        assertThat(jdbc.queryForObject("SELECT withdrawn_by FROM scientific_consent_evidence", UUID.class)).isEqualTo(user);
        assertThat(consent.getStatus()).isEqualTo("WITHDRAWN");
        verify(consents).saveAndFlush(consent);
        jdbc.update("UPDATE scientific_assignments SET active=TRUE");
        mvc.perform(submission(token)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/scientific-applications/{id}/history", assignment)
                .header("Authorization", token).header("X-Tenant-Id", tenant)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/scientific-applications/{id}/administrations/ADMIN-001/snapshot", assignment)
                .header("Authorization", token).header("X-Tenant-Id", tenant)).andExpect(status().isForbidden());
        verifyNoInteractions(submissions);
    }

    @Test
    void anotherSignerCannotWithdraw() throws Exception {
        mvc.perform(post("/api/v1/scientific-applications/consents/{id}/withdraw", evidence)
                .header("Authorization", signedToken(UUID.randomUUID(), tenant, "ilp-scientific-api", Instant.now().plusSeconds(300)))
                .header("X-Tenant-Id", tenant)).andExpect(status().isForbidden());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM scientific_consent_evidence WHERE withdrawn_at IS NOT NULL", Integer.class)).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/assessment-responses/ADMIN-001", "/api/v1/participants/SUBJECT-001/assessment-scientific-history",
            "/api/v1/datasets/educational-ml/training-snapshot", "/api/v1/students/STUDENT-001/assessment-history",
            "/api/v1/fieldwork/consents"})
    void legacyRoutesCannotBypassBoundary(String path) throws Exception {
        mvc.perform(get(path).header("Authorization", token)).andExpect(status().isForbidden());
    }
}
