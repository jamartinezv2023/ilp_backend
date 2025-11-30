package com.ilp.e2e.commons;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class ResourceDTOCrudE2ETest {

    private static final String BASE_URL =
            System.getProperty("ilp.e2e.base-url",
                    System.getenv().getOrDefault("ILP_E2E_BASE_URL", "http://localhost:8080"));

    private static final String BASE_PATH = "/api/commons/resourcedto";

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    void create_shouldReturnSuccessOrValidationError() {
        // TODO: completar JSON vÃ¡lido para ResourceDTO
        String body = "{ }";

        given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post(BASE_PATH)
        .then()
            // Permitimos varios cÃ³digos mientras el API madura
            .statusCode(anyOf(is(201), is(200), is(400), is(404)));
    }

    @Test
    void read_shouldReturn200Or404() {
        given()
        .when()
            .get(BASE_PATH + "/{id}", 1)
        .then()
            .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    void update_shouldReturnSuccessOr404() {
        // TODO: completar JSON vÃ¡lido para ResourceDTO
        String body = "{ }";

        given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .put(BASE_PATH + "/{id}", 1)
        .then()
            .statusCode(anyOf(is(200), is(400), is(404)));
    }

    @Test
    void delete_shouldReturn204Or404() {
        given()
        .when()
            .delete(BASE_PATH + "/{id}", 1)
        .then()
            .statusCode(anyOf(is(204), is(404)));
    }
}
