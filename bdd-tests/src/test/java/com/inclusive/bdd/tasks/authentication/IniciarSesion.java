package com.inclusive.bdd.tasks.authentication;

import com.inclusive.bdd.models.authentication.LoginCredentials;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

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

        actor.remember(
                "authenticatedUser",
                credentials.username()
        );
    }
}
