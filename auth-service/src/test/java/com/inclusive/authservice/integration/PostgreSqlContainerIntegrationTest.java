package com.inclusive.authservice.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@EnabledIfEnvironmentVariable(named = "ENABLE_TESTCONTAINERS", matches = "true")
class PostgreSqlContainerIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("ilp_test")
            .withUsername("ilp")
            .withPassword("ilp");

    @Test
    void shouldStartPostgresAndExecuteSimpleQuery() throws Exception {
        try (
                Connection connection = DriverManager.getConnection(
                        POSTGRES.getJdbcUrl(),
                        POSTGRES.getUsername(),
                        POSTGRES.getPassword()
                );
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT 1")
        ) {
            assertTrue(resultSet.next());
            assertTrue(resultSet.getInt(1) == 1);
        }
    }
}