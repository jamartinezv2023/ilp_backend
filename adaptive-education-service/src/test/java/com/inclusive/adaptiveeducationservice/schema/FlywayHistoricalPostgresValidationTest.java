package com.inclusive.adaptiveeducationservice.schema;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(
        named = "RUN_SCHEMA_HISTORY_VALIDATION",
        matches = "true"
)
class FlywayHistoricalPostgresValidationTest {

    @Test
    void shouldValidateHistoricalSchemaWithoutMutation() {
        String url = requiredEnvironment(
                "HISTORICAL_POSTGRES_URL"
        );

        String username = requiredEnvironment(
                "HISTORICAL_POSTGRES_USERNAME"
        );

        String password = requiredEnvironment(
                "HISTORICAL_POSTGRES_PASSWORD"
        );

        Flyway flyway = Flyway.configure()
                .dataSource(
                        url,
                        username,
                        password
                )
                .locations("classpath:db/migration")
                .cleanDisabled(true)
                .load();

        assertThatCode(flyway::validate)
                .doesNotThrowAnyException();
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