package com.inclusive.bdd.questions.authentication;

import io.restassured.response.Response;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class UsuarioAutenticado implements Question<Boolean> {

    public static UsuarioAutenticado enLaPlataforma() {
        return new UsuarioAutenticado();
    }

    @Override
    public Boolean answeredBy(Actor actor) {

        Response response = actor.recall("lastResponse");

        return response != null
                && response.statusCode() == 200
                && response.jsonPath().getString("accessToken") != null
                && response.jsonPath().getString("refreshToken") != null;
    }
}
