package ilp.r9;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inclusive.adaptiveeducationservice.AdaptiveEducationServiceApplication;
import com.inclusive.adaptiveeducationservice.research.production.*;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.*;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission.*;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.GetParticipantAssessmentScientificHistoryService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.*;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.AssessmentScoringStrategy;
import com.inclusive.adaptiveeducationservice.assessmentresponse.service.AssessmentResponseService;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.*;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.*;
import com.inclusive.adaptiveeducationservice.fieldwork.adapter.out.persistence.researchidentity.*;
import com.inclusive.adaptiveeducationservice.student.entity.StudentProfileEntity;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/** Real production filter, token verification, authorization registry, consent, JPA submission and history.
 * Only the explicitly synthetic scoring strategy and collection policy are supplied by this test runtime.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.inclusive.adaptiveeducationservice", excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        AdaptiveEducationServiceApplication.class, ScientificProductionConfiguration.class
    })
})
@EntityScan("com.inclusive.adaptiveeducationservice")
@EnableJpaRepositories("com.inclusive.adaptiveeducationservice")
public class R9AdaptiveRuntime {
    static final String CODE = "ILP-SYNTHETIC-R9";
    static final String VERSION = "0.0.1-test";
    static final UUID TENANT = UUID.fromString("11111111-1111-4111-8111-111111111111");
    static final UUID OWNER = UUID.fromString("90000000-0000-4000-8000-000000000001");
    static final UUID DOCUMENT = UUID.fromString("90000000-0000-4000-8000-000000000010");
    public static void main(String[] args) throws Exception {
        var application = new SpringApplication(R9AdaptiveRuntime.class);
        application.addListeners((org.springframework.context.ApplicationListener<org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent>) event -> {
            var environment = event.getEnvironment();
            if (!"jdbc:h2:mem:r9_18084;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1".equals(environment.getProperty("spring.datasource.url"))
                || !"127.0.0.1".equals(environment.getProperty("server.address"))
                || !"18084".equals(environment.getProperty("server.port"))
                || !"never".equals(environment.getProperty("spring.sql.init.mode"))
                || !"false".equals(environment.getProperty("spring.flyway.enabled"))
                || !"false".equals(environment.getProperty("spring.liquibase.enabled"))) {
                throw new IllegalArgumentException("R9 runtime requires its isolated in-memory configuration");
            }
        });
        var context = application.run(args);
        Files.writeString(Path.of(context.getEnvironment().getRequiredProperty("r9.ready")), "READY");
    }
    @Bean ScientificTokenVerifier r9Verifier(Environment environment) throws Exception {
        String base = environment.getRequiredProperty("r9.auth-base");
        if (!base.equals("http://127.0.0.1:18083")) throw new IllegalArgumentException("Local fixture auth required");
        var response = HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(5)).build()
            .send(HttpRequest.newBuilder(URI.create(base + "/.well-known/jwks.json"))
                .header("X-Tenant-Id", TENANT.toString()).timeout(java.time.Duration.ofSeconds(10)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IllegalStateException("Local JWKS unavailable");
        var keys = JWKSet.parse(response.body()).getKeys();
        if (keys.size() != 1 || !(keys.get(0) instanceof RSAKey key)) throw new IllegalStateException("Unexpected local JWKS");
        return new ScientificTokenVerifier(key.toRSAPublicKey(), "urn:ilp:r9:isolated", "ilp-scientific-api");
    }
    @Bean FilterRegistrationBean<ScientificApiFilter> r9Boundary(ScientificTokenVerifier verifier) {
        var registration = new FilterRegistrationBean<>(new ScientificApiFilter(verifier));
        registration.addUrlPatterns("/api/*"); registration.setOrder(1); return registration;
    }
    @Bean @DependsOn("entityManagerFactory") ScientificAssignmentStore r9Assignments(JdbcTemplate jdbc) {
        // Additive test schema in a new in-memory database only, never a live migration.
        new ResourceDatabasePopulator(new ClassPathResource(
            "db/scientific-production/V1__scientific_authorization_registry.sql"))
            .execute(Objects.requireNonNull(jdbc.getDataSource()));
        return new ScientificAssignmentStore(jdbc);
    }
    @Bean ScientificProductionService r9Scientific(ScientificAssignmentStore assignments,
        ResearchParticipantRepository participants, ConsentRecordRepository consents,
        ScientificParticipantIdentityPort identities, SubmitAssessmentService submissions,
        GetParticipantAssessmentScientificHistoryService histories, AssessmentResponseService responses) {
        return new ScientificProductionService(assignments, participants, consents, identities, submissions, histories, responses);
    }
    @Bean @Primary ControlledAssessmentModePolicy r9SyntheticPolicy() {
        return new ControlledAssessmentModePolicy() {
            @Override public boolean validateAndIsDemo(SubmitAssessmentRequest request) {
                if (!CODE.equals(request.assessmentCode()) || !VERSION.equals(request.assessmentVersion())
                    || !request.participantId().startsWith("SYNTHETIC-R9-")
                    || !"TEST_ONLY".equals(request.context().get("fieldworkPhase"))
                    || !"R9_ISOLATED".equals(request.context().get("source"))) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "R9_SYNTHETIC_ONLY");
                }
                return false;
            }
        };
    }
    @Bean AssessmentScoringStrategy r9Scoring() {
        return new AssessmentScoringStrategy() {
            public boolean supports(String code) { return CODE.equals(code); }
            public AssessmentResult score(AssessmentDefinition definition, AssessmentSubmission submission) {
                return new AssessmentResult(submission.administrationId(), submission.participantId(), CODE, VERSION,
                    "SYNTHETIC_NO_EDUCATIONAL_INTERPRETATION", Map.of("TEST_ONLY", 1.0), Map.of(),
                    List.of(), "R9-SYNTHETIC-SCORING", Instant.now());
            }
        };
    }
    @Bean CommandLineRunner r9Fixture(ScientificAssignmentStore assignments, JdbcTemplate jdbc,
        ResearchParticipantRepository participants, ConsentRecordRepository consents,
        ResearchSubjectIdentityJpaRepository identities, StudentProfileRepository students,
        AssessmentDefinitionRepository definitions, ObjectMapper mapper, Environment environment) {
        return args -> {
            var definition = new AssessmentDefinitionEntity(CODE, CODE, "Synthetic transport test",
                "Not an educational instrument", "GENERIC", VERSION, true, 1,
                "Choose one synthetic answer", Instant.now());
            var question = new AssessmentQuestionEntity("R9-Q1", 1, "Synthetic question", "TEST_ONLY", "Test only",
                true, "SINGLE_CHOICE", 1);
            question.addOption(new AssessmentOptionEntity("R9-A", "Synthetic A", "A", 1, 1));
            question.addOption(new AssessmentOptionEntity("R9-B", "Synthetic B", "B", 1, 2));
            definition.addQuestion(question); definitions.saveAndFlush(definition);
            jdbc.update("INSERT INTO scientific_memberships VALUES (?, ?, TRUE, ?, CURRENT_TIMESTAMP)", TENANT, OWNER, OWNER);
            String documentText = "Synthetic adult consent, isolated transport test only";
            String digest = ScientificSnapshot.sha256(documentText);
            jdbc.update("INSERT INTO scientific_consent_documents VALUES (?, ?, 'r9-test', ?, ?, ?, CURRENT_TIMESTAMP, TRUE)",
                DOCUMENT, TENANT, documentText, digest, OWNER);
            jdbc.update("INSERT INTO scientific_instrument_permissions VALUES (?, ?, ?, TRUE, ?, ?, CURRENT_TIMESTAMP)",
                TENANT, CODE, VERSION, "SYNTHETIC_NOT_ORIGINAL_INSTRUMENT", OWNER);
            var sessions = new LinkedHashMap<String, Object>();
            int index = 1;
            for (String locale : List.of("es360", "es1440", "en360", "en1440")) {
                String student = "SYNTHETIC-R9-" + locale;
                students.saveAndFlush(new StudentProfileEntity(student, "Synthetic High", "TEST", 18,
                    "TEST_ONLY", "TEST_ONLY", "LOW", List.of(), List.of()));
                var participant = participants.saveAndFlush(new ResearchParticipant(student, "APPROVED", "TEST_ONLY"));
                var consent = consents.saveAndFlush(new ConsentRecord(student, "SYNTHETIC_RESEARCH", "APPROVED"));
                UUID subject = UUID.randomUUID();
                identities.saveAndFlush(new ResearchSubjectIdentityEntity(participant.getParticipantUuid(), subject, LocalDateTime.now(), null));
                UUID assignment = UUID.fromString("90000000-0000-4000-8000-00000000002" + index++);
                UUID evidence = UUID.randomUUID();
                jdbc.update("INSERT INTO scientific_participant_bindings VALUES (?, ?, ?, ?, TRUE, ?, CURRENT_TIMESTAMP)",
                    TENANT, participant.getParticipantUuid(), student, subject.toString(), OWNER);
                jdbc.update("""
                    INSERT INTO scientific_consent_evidence VALUES (?, ?, ?, ?, ?, 'SYNTHETIC_RESEARCH', ?, ?,
                        'ADULT_SELF', 'TEST_ONLY', CURRENT_TIMESTAMP, NULL, NULL)
                    """, evidence, TENANT, participant.getParticipantUuid(), DOCUMENT, consent.getConsentId(), OWNER, digest);
                jdbc.update("""
                    INSERT INTO scientific_assignments VALUES (?, ?, ?, ?, ?, ?, ?, TRUE, TRUE, TRUE, TRUE, ?, ?, CURRENT_TIMESTAMP)
                    """, assignment, TENANT, OWNER, participant.getParticipantUuid(), evidence, CODE, VERSION,
                        java.sql.Timestamp.from(Instant.now().plusSeconds(3600)), OWNER);
                sessions.put(locale, Map.of("assignmentId", assignment.toString(), "participantId", student,
                    "researchParticipantUuid", participant.getParticipantUuid().toString(), "consentId", consent.getConsentId().toString(),
                    "consentVersion", "r9-test", "assessmentCode", CODE, "assessmentVersion", VERSION,
                    "researchSubjectId", subject.toString(), "evidenceId", evidence.toString()));
            }
            Files.writeString(Path.of(environment.getRequiredProperty("r9.fixture")), mapper.writeValueAsString(sessions));
        };
    }
}
