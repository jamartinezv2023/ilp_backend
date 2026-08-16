package com.inclusive.bdd.stepdefinitions.research;

import com.inclusive.bdd.questions.research.CodigoDeReclutamientoAusente;
import com.inclusive.bdd.questions.research.IdentidadDeInvestigacionAsignada;
import com.inclusive.bdd.questions.research.IdentidadDeInvestigacionConservada;

import com.inclusive.bdd.tasks.research.AsignarIdentidadDeInvestigacion;
import com.inclusive.bdd.tasks.research.IncorporarParticipante;
import com.inclusive.bdd.tasks.research.RegistrarConsentimientoVigente;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

import java.util.UUID;

import static org.hamcrest.Matchers.is;

public class ResearchIdentityStepDefinitions {

    private Actor investigador;

    @Before("@research")
    public void prepararActor() {
        OnStage.setTheStage(
                new OnlineCast()
        );

        String baseUrl =
                System.getProperty(
                        "services.adaptive.base-url",
                        "http://localhost:18083"
                );

        investigador =
                OnStage.theActorCalled(
                        "quien realiza la investigación"
                );

        investigador.can(
                CallAnApi.at(
                        baseUrl
                )
        );
    }

    @Dado(
            "que un participante cuenta con consentimiento vigente para el estudio"
    )
    public void participanteConConsentimientoVigente() {
        String participantCode =
                "PILOT-" +
                        UUID.randomUUID();

        investigador.attemptsTo(
                RegistrarConsentimientoVigente.para(
                        participantCode
                ),
                IncorporarParticipante.conCodigo(
                        participantCode
                )
        );
    }

    @Dado(
            "el participante no tiene una identidad de investigación asignada"
    )
    public void participanteSinIdentidadDeInvestigacion() {
        investigador.remember(
                "research.identity.assigned",
                false
        );
    }

    @Cuando(
            "quien realiza la investigación incorpora al participante al conjunto de datos del estudio"
    )
    public void investigadorIncorporaParticipanteAlDataset() {
        investigador.attemptsTo(
                AsignarIdentidadDeInvestigacion
                        .alParticipante()
        );
    }

    @Entonces(
            "el participante queda asociado con una identidad de investigación"
    )
    public void participanteAsociadoConIdentidadDeInvestigacion() {
        investigador.should(
                net.serenitybdd.screenplay.GivenWhenThen.seeThat(
                        IdentidadDeInvestigacionAsignada
                                .alParticipante(),
                        is(true)
                )
        );
    }

    @Entonces(
            "la identidad de investigación no contiene el código utilizado durante el reclutamiento"
    )
    public void identidadNoContieneCodigoDeReclutamiento() {
        investigador.should(
                net.serenitybdd.screenplay.GivenWhenThen.seeThat(
                        CodigoDeReclutamientoAusente
                                .enLaIdentidadDeInvestigacion(),
                        is(true)
                )
        );
    }

    @Dado(
            "que un participante tiene una identidad de investigación asignada"
    )
    public void unParticipanteTieneIdentidadDeInvestigacionAsignada() {
        participanteTieneIdentidadDeInvestigacionAsignada();
    }

    @Dado(
            "el participante tiene una identidad de investigación asignada"
    )
    public void participanteTieneIdentidadDeInvestigacionAsignada() {

        String participantUuid =
                investigador.recall(
                        "research.participant.uuid"
                );

        /*
         * This Given is used both after an explicit
         * consent/participant setup and as an autonomous
         * precondition in the withdrawal scenario.
         */
        if (
                participantUuid == null
                        || participantUuid.isBlank()
        ) {

            String participantCode =
                    "BDD-RESEARCH-"
                            + UUID.randomUUID();

            investigador.attemptsTo(
                    RegistrarConsentimientoVigente.para(
                            participantCode
                    ),
                    IncorporarParticipante.conCodigo(
                            participantCode
                    )
            );
        }

        investigador.attemptsTo(
                AsignarIdentidadDeInvestigacion
                        .alParticipante()
        );

        String firstResearchSubjectId =
                investigador.recall(
                        "research.identity.subject-id"
                );

        if (
                firstResearchSubjectId == null
                        || firstResearchSubjectId.isBlank()
        ) {
            throw new IllegalStateException(
                    "first research subject id is required"
            );
        }

        investigador.remember(
                "research.identity.previous-subject-id",
                firstResearchSubjectId
        );
    }

    @Cuando(
            "quien realiza la investigación incorpora nuevamente al participante al conjunto de datos del estudio"
    )
    public void investigadorReincorporaParticipanteAlDataset() {
        investigador.attemptsTo(
                AsignarIdentidadDeInvestigacion
                        .alParticipante()
        );
    }

    @Entonces(
            "el participante conserva la identidad de investigación previamente asignada"
    )
    public void participanteConservaIdentidadPreviamenteAsignada() {
        investigador.should(
                net.serenitybdd.screenplay.GivenWhenThen.seeThat(
                        IdentidadDeInvestigacionConservada
                                .entreIncorporaciones(),
                        is(true)
                )
        );
    }

    @Dado(
            "que una persona no cuenta con consentimiento vigente para el estudio"
    )
    public void personaSinConsentimientoVigente() {
        String participantCode =
                "BDD-NO-CONSENT-"
                        + UUID.randomUUID();

        investigador.attemptsTo(
                IncorporarParticipante.conCodigo(
                        participantCode
                )
        );
    }

}
