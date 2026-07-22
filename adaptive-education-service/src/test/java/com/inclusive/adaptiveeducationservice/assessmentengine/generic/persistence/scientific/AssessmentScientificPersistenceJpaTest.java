package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentInterpretationEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentScientificResultEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentScoreItemEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity.AssessmentSubmissionContextEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentScientificResultRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentSubmissionContextRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
        properties = {
                "spring.flyway.enabled=false",
                "spring.sql.init.mode=never",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.datasource.url=jdbc:h2:mem:scientificdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE"
        }
)
@ActiveProfiles("local")
class AssessmentScientificPersistenceJpaTest {

    @Autowired
    private AssessmentScientificResultRepository resultRepository;

    @Autowired
    private AssessmentSubmissionContextRepository contextRepository;

    @Test
    void shouldPersistCompleteScientificObservation() {
        String administrationId = "ADMIN-JPA-001";
        String resultId = "RESULT-JPA-001";

        Instant submittedAt =
                Instant.parse("2026-07-21T10:00:00Z");

        Instant calculatedAt =
                Instant.parse("2026-07-21T10:00:01Z");

        AssessmentScientificResultEntity result =
                new AssessmentScientificResultEntity(
                        resultId,
                        administrationId,
                        "ST-001",
                        "KOLB_V1",
                        "1.0",
                        "DIVERGENT",
                        "KOLB_BASELINE_V1",
                        "KOLB_INTERPRETATION_V1",
                        calculatedAt,
                        submittedAt,
                        submittedAt
                );

        result.addScore(
                score(
                        administrationId,
                        "CE",
                        48.0
                )
        );

        result.addScore(
                score(
                        administrationId,
                        "RO",
                        36.0
                )
        );

        result.addScore(
                score(
                        administrationId,
                        "AC",
                        24.0
                )
        );

        result.addScore(
                score(
                        administrationId,
                        "AE",
                        12.0
                )
        );

        result.addScore(
                score(
                        administrationId,
                        "AC_MINUS_CE",
                        -24.0
                )
        );

        result.addScore(
                score(
                        administrationId,
                        "AE_MINUS_RO",
                        -24.0
                )
        );

        result.addInterpretation(
                new AssessmentInterpretationEntity(
                        "INTERPRETATION-JPA-001",
                        administrationId,
                        "PRIMARY_PROFILE",
                        "Perfil divergente",
                        "KOLB_INTERPRETATION_V1"
                )
        );

        resultRepository.saveAndFlush(result);

        AssessmentSubmissionContextEntity context =
                new AssessmentSubmissionContextEntity(
                        "CONTEXT-JPA-001",
                        administrationId,
                        submittedAt
                );

        context.defineAcademicContext(
                "INST-001",
                null,
                null,
                "10-1",
                "COHORT-001",
                "TEACHER-001",
                "10",
                "2026",
                "PILOT-1"
        );

        context.defineFieldworkContext(
                "PILOT",
                null,
                null,
                "CONSENT-001",
                "1.0",
                "ETHICS-001"
        );

        context.defineTechnicalContext(
                "WEB",
                "SUPERVISED",
                "es",
                "DESKTOP",
                "Chrome",
                "Windows",
                "America/Bogota",
                "0.9.0"
        );

        context.defineTiming(
                submittedAt.minusSeconds(600),
                600L
        );

        context.replaceContextJson(
                Map.of(
                        "featureCutoffPolicy",
                        "SUBMITTED_AT",
                        "deviceCategory",
                        "DESKTOP"
                )
        );

        contextRepository.saveAndFlush(context);

        var persisted =
                resultRepository
                        .findByAdministrationId(
                                administrationId
                        )
                        .orElseThrow();

        var persistedContext =
                contextRepository
                        .findByAdministrationId(
                                administrationId
                        )
                        .orElseThrow();

        assertThat(
                resultRepository.existsByAdministrationId(
                        administrationId
                )
        ).isTrue();

        assertThat(
                contextRepository.existsByAdministrationId(
                        administrationId
                )
        ).isTrue();

        assertThat(persisted.getScores())
                .hasSize(6);

        assertThat(
                persisted.getScores()
                        .stream()
                        .map(
                                AssessmentScoreItemEntity::getDimensionCode
                        )
        )
                .containsExactlyInAnyOrder(
                        "CE",
                        "RO",
                        "AC",
                        "AE",
                        "AC_MINUS_CE",
                        "AE_MINUS_RO"
                );

        assertThat(persisted.getFeatureCutoffAt())
                .isEqualTo(submittedAt);

        assertThat(persistedContext.getFeatureCutoffAt())
                .isEqualTo(submittedAt);

        assertThat(persisted.getInterpretations())
                .hasSize(1);

        assertThat(persisted.getPrimaryProfile())
                .isEqualTo("DIVERGENT");

        assertThat(persistedContext.getInstitutionId())
                .isEqualTo("INST-001");

        assertThat(persistedContext.getDurationSeconds())
                .isEqualTo(600L);

        assertThat(persistedContext.getContextJson())
                .containsEntry(
                        "featureCutoffPolicy",
                        "SUBMITTED_AT"
                );
    }

    private AssessmentScoreItemEntity score(
            String administrationId,
            String dimension,
            Double value
    ) {
        return new AssessmentScoreItemEntity(
                "SCORE-JPA-" + dimension,
                administrationId,
                dimension,
                value
        );
    }
}