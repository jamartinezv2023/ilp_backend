package com.inclusive.bdd.tasks.research;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class IncorporarParticipante
        implements Task {

    private final String participantCode;

    public IncorporarParticipante(
            String participantCode
    ) {
        this.participantCode =
                participantCode;
    }

    public static IncorporarParticipante conCodigo(
            String participantCode
    ) {
        return instrumented(
                IncorporarParticipante.class,
                participantCode
        );
    }

    @Override
    public <T extends Actor> void performAs(
            T actor
    ) {
        actor.attemptsTo(
                Post.to(
                        "/api/v1/fieldwork/participants"
                ).with(
                        request ->
                                request
                                        .contentType(
                                                "application/json"
                                        )
                                        .body(
                                                """
                                                {
                                                  "participantCode": "%s",
                                                  "cohort": "PILOT-1"
                                                }
                                                """.formatted(
                                                        participantCode
                                                )
                                        )
                )
        );

        SerenityRest.lastResponse()
                .then()
                .statusCode(201);

        String participantUuid =
                SerenityRest
                        .lastResponse()
                        .jsonPath()
                        .getString(
                                "participantUuid"
                        );

        actor.remember(
                "research.participant.uuid",
                participantUuid
        );

        actor.remember(
                "research.participant.code",
                participantCode
        );
    }
}
