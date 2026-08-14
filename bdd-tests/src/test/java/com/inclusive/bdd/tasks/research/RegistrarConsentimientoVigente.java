package com.inclusive.bdd.tasks.research;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class RegistrarConsentimientoVigente
        implements Task {

    private final String participantCode;

    public RegistrarConsentimientoVigente(
            String participantCode
    ) {
        this.participantCode =
                participantCode;
    }

    public static RegistrarConsentimientoVigente para(
            String participantCode
    ) {
        return instrumented(
                RegistrarConsentimientoVigente.class,
                participantCode
        );
    }

    @Override
    public <T extends Actor> void performAs(
            T actor
    ) {
        actor.attemptsTo(
                Post.to(
                        "/api/v1/fieldwork/consents"
                ).with(
                        request ->
                                request
                                        .contentType(
                                                "application/json"
                                        )
                                        .body(
                                                """
                                                {
                                                  "participantCode": "__PARTICIPANT_CODE__",
                                                  "consentType": "RESEARCH",
                                                  "status": "APPROVED"
                                                }
                                                """.replace("__PARTICIPANT_CODE__", participantCode)
                                        )
                )
        );

        SerenityRest.lastResponse()
                .then()
                .statusCode(201);

        String consentId =
                SerenityRest
                        .lastResponse()
                        .jsonPath()
                        .getString(
                                "consentId"
                        );

        if (
                consentId == null
                        || consentId.isBlank()
        ) {
            throw new IllegalStateException(
                    "research consent id is required"
            );
        }

        actor.remember(
                "research.consent.id",
                consentId
        );

        actor.remember(
                "research.consent.status",
                "APPROVED"
        );
    }
}
