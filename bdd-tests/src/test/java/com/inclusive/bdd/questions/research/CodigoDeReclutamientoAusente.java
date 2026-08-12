package com.inclusive.bdd.questions.research;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class CodigoDeReclutamientoAusente
        implements Question<Boolean> {

    public static CodigoDeReclutamientoAusente
    enLaIdentidadDeInvestigacion() {
        return new CodigoDeReclutamientoAusente();
    }

    @Override
    public Boolean answeredBy(
            Actor actor
    ) {
        String participantCode =
                actor.recall(
                        "research.participant.code"
                );

        String responseBody =
                actor.recall(
                        "research.identity.response-body"
                );

        if (
                participantCode == null
                        || participantCode.isBlank()
        ) {
            return false;
        }

        if (
                responseBody == null
                        || responseBody.isBlank()
        ) {
            return false;
        }

        return !responseBody.contains(
                participantCode
        );
    }
}
