package com.inclusive.bdd.tasks.authentication;

import com.inclusive.bdd.models.authentication.LoginCredentials;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.thucydides.model.environment.SystemEnvironmentVariables;

import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class IniciarSesion implements Task {

    private final LoginCredentials credentials;

    public IniciarSesion(LoginCredentials credentials) {
        this.credentials = credentials;
    }

    public static IniciarSesion conCredenciales(
            LoginCredentials credentials
    ) {
        return instrumented(
                IniciarSesion.class,
                credentials
        );
    }

    @Override
    public <T extends Actor> void performAs(T actor) {

        SerenityRest.given()
                .baseUri(SystemEnvironmentVariables.createEnvironmentVariables().getProperty("services.auth.base-url"))
                .header("X-Tenant-Id", credentials.tenantId())
                .contentType("application/json")
                .body(Map.of(
                        "email", credentials.email(),
                        "password", credentials.password()
                ))
                .post("/auth/login");

        actor.remember(
                "lastResponse",
                SerenityRest.lastResponse()
        );
    }
}
