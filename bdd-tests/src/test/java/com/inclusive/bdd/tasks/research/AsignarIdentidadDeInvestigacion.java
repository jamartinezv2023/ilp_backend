package com.inclusive.bdd.tasks.research;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class AsignarIdentidadDeInvestigacion
        implements Task {

    public static AsignarIdentidadDeInvestigacion alParticipante() {
        return instrumented(
                AsignarIdentidadDeInvestigacion.class
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

        SerenityRest.lastResponse()
                .then()
                .statusCode(200);

        String researchSubjectId =
                SerenityRest
                        .lastResponse()
                        .jsonPath()
                        .getString(
                                "researchSubjectId"
                        );

        Boolean active =
                SerenityRest
                        .lastResponse()
                        .jsonPath()
                        .getBoolean(
                                "active"
                        );

        String responseBody =
                SerenityRest
                        .lastResponse()
                        .getBody()
                        .asString();

        actor.remember(
                "research.identity.subject-id",
                researchSubjectId
        );

        actor.remember(
                "research.identity.active",
                active
        );

        actor.remember(
                "research.identity.response-body",
                responseBody
        );

        actor.remember(
                "research.identity.assigned",
                true
        );
    }
}
