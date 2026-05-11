package com.inclusive.bdd.questions.authentication;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class UsuarioAutenticado implements Question<Boolean> {

    public static UsuarioAutenticado enLaPlataforma() {
        return new UsuarioAutenticado();
    }

    @Override
    public Boolean answeredBy(Actor actor) {

        return actor.recall("authenticatedUser") != null;
    }
}
