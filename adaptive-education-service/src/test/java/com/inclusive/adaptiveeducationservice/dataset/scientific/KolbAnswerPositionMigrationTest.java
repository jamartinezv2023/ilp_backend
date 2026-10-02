package com.inclusive.adaptiveeducationservice.dataset.scientific;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
@Testcontainers
class KolbAnswerPositionMigrationTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");
    @BeforeEach
    void prepareLegacyTable() throws SQLException {
        try (var connection = connect();
                var statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS public.kolb_assessment_answers");
            statement.execute("""
                    CREATE TABLE public.kolb_assessment_answers (
                        assessment_id varchar(255),
                        answer_value integer
                    )
                    """);
        }
    }
    @Test
    void shouldAddPositionToEmptyTable() throws SQLException, IOException {
        try (var connection = connect();
                var statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            statement.execute(migration());
            connection.commit();
            try (var columns = statement.executeQuery("""
                    SELECT data_type
                    FROM information_schema.columns
                    WHERE table_schema = 'public'
                      AND table_name = 'kolb_assessment_answers'
                      AND column_name = 'answer_position'
                    """)) {
                assertThat(columns.next()).isTrue();
                assertThat(columns.getString(1)).isEqualTo("integer");
            }
        }
    }
    @Test
    void shouldRejectHistoricalAnswersWithoutChangingThem()
            throws SQLException, IOException {
        try (var connection = connect();
                var statement = connection.createStatement()) {
            statement.execute("""
                    INSERT INTO public.kolb_assessment_answers
                        (assessment_id, answer_value)
                    VALUES ('HISTORICAL-001', 4), ('HISTORICAL-001', 3)
                    """);
            var sql = migration();
            connection.setAutoCommit(false);
            assertThatThrownBy(() -> statement.execute(sql))
                    .isInstanceOf(SQLException.class)
                    .hasMessageContaining("existing responses require");
            connection.rollback();
            try (var answers = statement.executeQuery("""
                    SELECT assessment_id, answer_value
                    FROM public.kolb_assessment_answers
                    ORDER BY answer_value
                    """)) {
                assertThat(answers.next()).isTrue();
                assertThat(answers.getString(1)).isEqualTo("HISTORICAL-001");
                assertThat(answers.getInt(2)).isEqualTo(3);
                assertThat(answers.next()).isTrue();
                assertThat(answers.getString(1)).isEqualTo("HISTORICAL-001");
                assertThat(answers.getInt(2)).isEqualTo(4);
                assertThat(answers.next()).isFalse();
            }
            try (var columns = statement.executeQuery("""
                    SELECT count(*)
                    FROM information_schema.columns
                    WHERE table_schema = 'public'
                      AND table_name = 'kolb_assessment_answers'
                      AND column_name = 'answer_position'
                    """)) {
                assertThat(columns.next()).isTrue();
                assertThat(columns.getInt(1)).isZero();
            }
        }
    }
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(
                POSTGRES.getJdbcUrl(),
                POSTGRES.getUsername(),
                POSTGRES.getPassword()
        );
    }
    private String migration() throws IOException {
        try (var input = getClass().getResourceAsStream(
                "/db/migration/V4__add_kolb_answer_position.sql"
        )) {
            if (input == null) {
                throw new IOException("Kolb migration resource is missing");
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
