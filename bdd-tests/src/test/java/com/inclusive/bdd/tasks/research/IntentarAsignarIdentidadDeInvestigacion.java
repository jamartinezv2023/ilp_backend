package com.inclusive.bdd.tasks.research;

import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class IntentarAsignarIdentidadDeInvestigacion
        implements Task {

    public static IntentarAsignarIdentidadDeInvestigacion
    alParticipante() {
        return instrumented(
                IntentarAsignarIdentidadDeInvestigacion.class
        );
    }

    @Override
    public <T extends Actor> void performAs(
            T actor
    ) {
        String participantUuid =
                actor.recall(
                        "research.participant.uuid"
                );

        if (
                participantUuid == null
                        || participantUuid.isBlank()
        ) {
            throw new IllegalStateException(
                    "research participant UUID is required"
            );
        }

        actor.attemptsTo(
                Post.to(
                        "/api/v1/fieldwork/research-participants/"
                                + participantUuid
                                + "/research-identity"
                )
        );

        Response response =
                SerenityRest.lastResponse();

        actor.remember(
                "research.identity.attempt.status-code",
                response.statusCode()
        );

        actor.remember(
                "research.identity.attempt.response-body",
                response.getBody().asString()
        );
    }
}
