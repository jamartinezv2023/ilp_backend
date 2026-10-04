package com.inclusive.adaptiveeducationservice.research.production;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;

/** Runs the same authorization/withdrawal contract against an ephemeral PostgreSQL schema. */
@Testcontainers
class ScientificProductionPostgresTest extends ScientificProductionHttpTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @Override
    protected DriverManagerDataSource createDataSource() {
        var source = new DriverManagerDataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
        var jdbc = new JdbcTemplate(source);
        for (String table : List.of("scientific_assignments", "scientific_instrument_permissions",
                "scientific_consent_evidence", "scientific_consent_documents",
                "scientific_participant_bindings", "scientific_memberships")) {
            jdbc.execute("DROP TABLE IF EXISTS " + table);
        }
        return source;
    }
}
