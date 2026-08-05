package com.inclusive.adaptiveeducationservice.schema;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@EnabledIfEnvironmentVariable(
        named = "RUN_SCHEMA_BOOTSTRAP_IT",
        matches = "true"
)
class FlywayEmptyPostgresBootstrapTest {

    @Test
    void shouldBootstrapEmptyPostgresAtVersionTwo() {
        String url = requiredEnvironment(
                "TEST_POSTGRES_URL"
        );

        String username = requiredEnvironment(
                "TEST_POSTGRES_USERNAME"
        );

        String password = requiredEnvironment(
                "TEST_POSTGRES_PASSWORD"
        );

        DataSource dataSource =
                new DriverManagerDataSource(
                        url,
                        username,
                        password
                );

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .cleanDisabled(true)
                .load();

        flyway.migrate();

        JdbcTemplate jdbcTemplate =
                new JdbcTemplate(dataSource);

        assertThat(tableExists(
                jdbcTemplate,
                "assessment_responses"
        )).isTrue();

        assertThat(tableExists(
                jdbcTemplate,
                "assessment_results"
        )).isTrue();

        assertThat(tableExists(
                jdbcTemplate,
                "scientific_feature_vectors"
        )).isTrue();

        Integer successfulMigrations =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM flyway_schema_history
                        WHERE success = TRUE
                        """,
                        Integer.class
                );

        assertThat(successfulMigrations)
                .isNotNull()
                .isPositive();
    }

    private boolean tableExists(
            JdbcTemplate jdbcTemplate,
            String tableName
    ) {
        Boolean exists =
                jdbcTemplate.queryForObject(
                        """
                        SELECT to_regclass(
                            'public.' || ?
                        ) IS NOT NULL
                        """,
                        Boolean.class,
                        tableName
                );

        return Boolean.TRUE.equals(exists);
    }

    private String requiredEnvironment(
            String name
    ) {
        String value = System.getenv(name);

        if (
                value == null ||
                value.isBlank()
        ) {
            throw new IllegalStateException(
                    "Missing environment variable: "
                            + name
            );
        }

        return value;
    }
}