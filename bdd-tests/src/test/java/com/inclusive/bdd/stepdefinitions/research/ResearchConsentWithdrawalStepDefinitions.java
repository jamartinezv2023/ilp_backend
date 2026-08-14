package com.inclusive.bdd.stepdefinitions.research;

import com.inclusive.bdd.tasks.research.IntentarRegistrarObservacionTrasRetiro;
import com.inclusive.bdd.tasks.research.RetirarConsentimiento;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;

import static org.hamcrest.Matchers.is;

public class ResearchConsentWithdrawalStepDefinitions {

    @Dado(
            "el participante ha retirado su consentimiento para el estudio"
    )
    public void participanteRetiraConsentimiento() {

        Actor investigador =
                OnStage.theActorInTheSpotlight();

        investigador.attemptsTo(
                RetirarConsentimiento
                        .delParticipante()
        );

        Boolean withdrawn =
                investigador.recall(
                        "research.consent.withdrawn"
                );

        investigador.should(
                net.serenitybdd.screenplay.GivenWhenThen.seeThat(
                        "el consentimiento queda retirado",
                        actor -> withdrawn,
                        is(true)
                )
        );
    }

    @Cuando(
            "quien realiza la investigación intenta registrar una nueva observación del participante"
    )
    public void investigadorIntentaRegistrarNuevaObservacion() {

        Actor investigador =
                OnStage.theActorInTheSpotlight();

        investigador.attemptsTo(
                IntentarRegistrarObservacionTrasRetiro
                        .delParticipante()
        );
    }

    @Entonces(
            "la observación no queda incorporada al conjunto de datos del estudio"
    )
    public void observacionNoQuedaIncorporada() {

        Actor investigador =
                OnStage.theActorInTheSpotlight();

        Integer submissionStatus =
                investigador.recall(
                        "research.observation.submission-status-code"
                );

        Integer observationLookupStatus =
                investigador.recall(
                        "research.observation.lookup-status-code"
                );

        investigador.should(
                net.serenitybdd.screenplay.GivenWhenThen.seeThat(
                        "la submission es rechazada por retiro del consentimiento",
                        actor -> submissionStatus,
                        is(403)
                ),
                net.serenitybdd.screenplay.GivenWhenThen.seeThat(
                        "ninguna observación científica queda persistida",
                        actor -> observationLookupStatus,
                        is(404)
                )
        );
    }
}
