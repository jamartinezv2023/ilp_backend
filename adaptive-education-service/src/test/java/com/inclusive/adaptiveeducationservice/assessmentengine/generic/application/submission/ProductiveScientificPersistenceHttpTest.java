package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.GetParticipantAssessmentScientificHistoryService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentScientificResultRepository;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.assessmentresponse.service.AssessmentResponseService;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.ConsentRecord;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.ResearchParticipant;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ConsentRecordRepository;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ResearchParticipantRepository;
import com.inclusive.adaptiveeducationservice.research.production.ScientificApiFilter;
import com.inclusive.adaptiveeducationservice.research.production.ScientificAssignmentStore;
import com.inclusive.adaptiveeducationservice.api.scientific.ScientificProductionController;
import com.inclusive.adaptiveeducationservice.research.production.ScientificProductionService;
import com.inclusive.adaptiveeducationservice.research.production.ScientificSnapshot;
import com.inclusive.adaptiveeducationservice.research.production.ScientificTokenVerifier;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Production HTTP boundary + real canonical persistence; instrument/scoring/identity use inherited synthetic fixtures. */
class ProductiveScientificPersistenceHttpTest extends SyntheticSubmissionHistoryHttpE2ETest {
    @Autowired private JdbcTemplate jdbc;
    @Autowired private SubmitAssessmentService canonical;
    @Autowired private GetParticipantAssessmentScientificHistoryService histories;
    @Autowired private AssessmentResponseService responses;
    @Autowired private ResearchParticipantRepository participants;
    @Autowired private ConsentRecordRepository consents;
    @Autowired private ScientificParticipantIdentityPort identities;
    @Autowired private PlatformTransactionManager transactions;
    @Autowired private SubmitAssessmentMapper mapper;
    @Autowired private GenericAssessmentEngine engine;
    @Autowired private AssessmentResponseRepository rawRecords;
    @Autowired private AssessmentScientificResultRepository scores;
    @Autowired private ObjectMapper json;
    protected MockMvc protectedMvc;
    protected SubmitAssessmentRequest protectedRequest;
    protected String bearer;
    protected final UUID tenant = UUID.fromString("00000000-0000-4000-8000-000000000001");
    private final UUID user = UUID.randomUUID();
    protected final UUID assignment = UUID.randomUUID();
    protected final UUID evidence = UUID.randomUUID();

    @BeforeEach
    void prepareProductionBoundary() throws Exception {
        for (String table : List.of("scientific_assignments", "scientific_instrument_permissions",
                "scientific_consent_evidence", "scientific_consent_documents",
                "scientific_participant_bindings", "scientific_memberships")) {
            jdbc.execute("DROP TABLE IF EXISTS " + table);
        }
        new ResourceDatabasePopulator(new ClassPathResource(
                "db/scientific-production/V1__scientific_authorization_registry.sql")).execute(jdbc.getDataSource());
        var original = (SubmitAssessmentRequest) ReflectionTestUtils.getField(this, "request");
        var participant = new ResearchParticipant("SYNTHETIC-PROD-" + UUID.randomUUID(), "APPROVED", "TEST");
        ReflectionTestUtils.setField(participant, "participantUuid", original.researchParticipantUuid());
        participants.saveAndFlush(participant);
        var consent = consents.saveAndFlush(new ConsentRecord(participant.getParticipantCode(), "SYNTHETIC_RESEARCH", "APPROVED"));
        var doc = UUID.randomUUID();
        String documentHash = ScientificSnapshot.sha256("Synthetic acceptance text");
        jdbc.update("INSERT INTO scientific_memberships VALUES (?, ?, TRUE, ?, CURRENT_TIMESTAMP)", tenant, user, user);
        jdbc.update("INSERT INTO scientific_participant_bindings VALUES (?, ?, ?, ?, TRUE, ?, CURRENT_TIMESTAMP)",
                tenant, original.researchParticipantUuid(), original.participantId(), "11111111-1111-1111-1111-111111111111", user);
        jdbc.update("INSERT INTO scientific_consent_documents VALUES (?, ?, 'v1', 'Synthetic acceptance text', ?, ?, CURRENT_TIMESTAMP, TRUE)", doc, tenant, documentHash, user);
        jdbc.update("""
                INSERT INTO scientific_consent_evidence VALUES (?, ?, ?, ?, ?, 'SYNTHETIC_RESEARCH', ?, ?,
                    'ADULT_SELF', 'TEST_ONLY', CURRENT_TIMESTAMP, NULL, NULL)
                """, evidence, tenant, original.researchParticipantUuid(), doc, consent.getConsentId(), user, documentHash);
        jdbc.update("INSERT INTO scientific_instrument_permissions VALUES (?, ?, ?, TRUE, 'SYNTHETIC_ONLY', ?, CURRENT_TIMESTAMP)",
                tenant, original.assessmentCode(), original.assessmentVersion(), user);
        jdbc.update("""
                INSERT INTO scientific_assignments VALUES (?, ?, ?, ?, ?, ?, ?, TRUE, TRUE, TRUE,
                    TRUE, ?, ?, CURRENT_TIMESTAMP)
                """, assignment, tenant, user, original.researchParticipantUuid(), evidence,
                original.assessmentCode(), original.assessmentVersion(), java.sql.Timestamp.from(Instant.now().plusSeconds(3600)), user);
        var context = new LinkedHashMap<>(original.context());
        context.put("consentId", consent.getConsentId().toString());
        context.put("consentVersion", "v1");
        protectedRequest = new SubmitAssessmentRequest(original.administrationId(), original.participantId(),
                original.researchParticipantUuid(), original.assessmentCode(), original.assessmentVersion(),
                original.responses(), context, original.submittedAt());
        var fixtureSubmission = mapper.toDomain(original);
        when(mapper.toDomain(any(SubmitAssessmentRequest.class))).thenAnswer(call -> {
            SubmitAssessmentRequest value = call.getArgument(0);
            return new AssessmentSubmission(value.administrationId(), value.participantId(), value.assessmentCode(),
                    value.assessmentVersion(), fixtureSubmission.responses(), value.context(), value.submittedAt());
        });
        when(engine.evaluate(any(com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition.class), any(AssessmentSubmission.class))).thenAnswer(call -> {
            AssessmentSubmission value = call.getArgument(1);
            return new AssessmentResult(value.administrationId(), value.participantId(), value.assessmentCode(),
                    value.assessmentVersion(), "SYNTHETIC_FEEDBACK", Map.of("SYNTHETIC_SCORE", 1.0),
                    Map.of("FEEDBACK", "TEST_ONLY"), List.of(), "SYNTHETIC_SCORING_TEST", value.submittedAt().plusSeconds(1));
        });
        var proxy = new ProxyFactory(new ScientificProductionService(new ScientificAssignmentStore(jdbc),
                participants, consents, identities, canonical, histories, responses));
        proxy.setProxyTargetClass(true);
        proxy.addAdvice(new TransactionInterceptor(transactions, new AnnotationTransactionAttributeSource()));
        var generator = KeyPairGenerator.getInstance("RSA"); generator.initialize(2048);
        var keys = generator.generateKeyPair();
        var token = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), new JWTClaimsSet.Builder()
                .issuer("urn:ilp:productive-test").audience("ilp-scientific-api").subject(user.toString())
                .claim("tenantId", tenant.toString()).issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plusSeconds(300))).build());
        token.sign(new RSASSASigner(keys.getPrivate())); bearer = "Bearer " + token.serialize();
        protectedMvc = MockMvcBuilders.standaloneSetup(new ScientificProductionController(
                (ScientificProductionService) proxy.getProxy())).addFilters(new ScientificApiFilter(
                        new ScientificTokenVerifier((RSAPublicKey) keys.getPublic(), "urn:ilp:productive-test", "ilp-scientific-api"))).build();
    }

    @Test
    void realPersistenceHistorySnapshotAndWithdrawal() throws Exception {
        protectedMvc.perform(post("/api/v1/assessment-submissions").header("Authorization", bearer)
                .header("X-Tenant-Id", tenant).header("X-Scientific-Grant", assignment)
                .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(protectedRequest)))
                .andExpect(status().isCreated());
        assertThat(rawRecords.count()).isEqualTo(1);
        assertThat(scores.count()).isEqualTo(1);
        String history = protectedMvc.perform(get("/api/v1/scientific-applications/{id}/history", assignment)
                .header("Authorization", bearer).header("X-Tenant-Id", tenant)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(json.readTree(history).size()).isEqualTo(1);
        String snapshotText = protectedMvc.perform(get("/api/v1/scientific-applications/{id}/administrations/{admin}/snapshot", assignment, protectedRequest.administrationId())
                .header("Authorization", bearer).header("X-Tenant-Id", tenant)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var snapshot = json.readTree(snapshotText);
        assertThat(snapshot.path("manifest").path("sha256").asText())
                .isEqualTo(ScientificSnapshot.sha256(snapshot.path("csv").asText()));
        assertThat(snapshot.path("manifest").path("rowCount").asInt()).isEqualTo(1);
        protectedMvc.perform(post("/api/v1/scientific-applications/consents/{id}/withdraw", evidence)
                .header("Authorization", bearer).header("X-Tenant-Id", tenant)).andExpect(status().isNoContent());
        protectedMvc.perform(get("/api/v1/scientific-applications/{id}/history", assignment)
                .header("Authorization", bearer).header("X-Tenant-Id", tenant)).andExpect(status().isForbidden());
        assertThat(rawRecords.count()).isEqualTo(1);
        assertThat(scores.count()).isEqualTo(1);
    }
}
