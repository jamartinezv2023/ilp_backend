package com.ilp.e2e;

import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;

public class SmokeTest {

    @Test
    void backendIsOnline() {
        given()
            .when().get("http://localhost:8080/actuator/health")
            .then()
            .statusCode(200);
    }
}
