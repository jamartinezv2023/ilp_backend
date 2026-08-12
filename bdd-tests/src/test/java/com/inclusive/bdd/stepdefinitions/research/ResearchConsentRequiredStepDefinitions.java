package com.inclusive.bdd.stepdefinitions.research;

import com.inclusive.bdd.tasks.research.IntentarAsignarIdentidadDeInvestigacion;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;

import static org.hamcrest.Matchers.is;

public class ResearchConsentRequiredStepDefinitions {

    @Cuando(
            "quien realiza la investigación intenta incorporarla al conjunto de datos del estudio"
    )
    public void investigadorIntentaIncorporarPersonaSinConsentimiento() {
        Actor investigador =
                OnStage.theActorInTheSpotlight();

        investigador.attemptsTo(
                IntentarAsignarIdentidadDeInvestigacion
                        .alParticipante()
        );
    }

    @Entonces(
            "la persona no queda asociada con una identidad de investigación"
    )
    public void personaNoQuedaAsociadaConIdentidadDeInvestigacion() {
        Actor investigador =
                OnStage.theActorInTheSpotlight();

        Integer statusCode =
                investigador.recall(
                        "research.identity.attempt.status-code"
                );

        investigador.should(
                net.serenitybdd.screenplay.GivenWhenThen.seeThat(
                        "el intento de asignación es rechazado con HTTP 403",
                        actor -> statusCode,
                        is(403)
                )
        );
    }
}
