package com.inclusive.bdd.stepdefinitions.authentication;

import com.inclusive.bdd.models.authentication.LoginCredentials;
import com.inclusive.bdd.questions.authentication.UsuarioAutenticado;
import com.inclusive.bdd.tasks.authentication.IniciarSesion;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.model.environment.SystemEnvironmentVariables;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginStepDefinitions {

    private Actor usuario;

    @Before
    public void configurarActor() {
        String baseUrl = SystemEnvironmentVariables
                .createEnvironmentVariables()
                .getProperty("restapi.baseurl", "http://localhost:8083");

        usuario = Actor.named("Usuario Plataforma")
                .whoCan(CallAnApi.at(baseUrl));
    }

    @Dado("el usuario tiene credenciales válidas")
    public void usuarioCredencialesValidas() {

        usuario.remember(
                "credentials",
                new LoginCredentials(
                        "admin@demo.com",
                        "Admin123*",
                        "00000000-0000-0000-0000-000000000001"
                )
        );
    }

    @Cuando("intenta iniciar sesión")
    public void intentaIniciarSesion() {

        LoginCredentials credentials =
                usuario.recall("credentials");

        usuario.attemptsTo(
                IniciarSesion.conCredenciales(credentials)
        );
    }

    @Entonces("accede correctamente a la plataforma")
    public void accedeCorrectamentePlataforma() {

        assertThat(
                UsuarioAutenticado
                        .enLaPlataforma()
                        .answeredBy(usuario)
        ).isTrue();
    }

    @Dado("el usuario ingresa credenciales no registradas")
    public void usuarioCredencialesNoRegistradas() {
    }

    @Entonces("visualiza un mensaje indicando que las credenciales no son válidas")
    public void mensajeCredencialesInvalidas() {
    }

    @Dado("el usuario se encuentra bloqueado")
    public void usuarioBloqueado() {
    }

    @Entonces("visualiza un mensaje indicando que el usuario está bloqueado")
    public void mensajeUsuarioBloqueado() {
    }
}
