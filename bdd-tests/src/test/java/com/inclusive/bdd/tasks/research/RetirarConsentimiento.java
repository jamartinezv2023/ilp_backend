package com.inclusive.bdd.tasks.research;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class RetirarConsentimiento
        implements Task {

    public static RetirarConsentimiento delParticipante() {
        return instrumented(
                RetirarConsentimiento.class
        );
    }

    @Override
    public <T extends Actor> void performAs(
            T actor
    ) {
        String consentId =
                actor.recall(
                        "research.consent.id"
                );

        if (
                consentId == null
                        || consentId.isBlank()
        ) {
            throw new IllegalStateException(
                    "research consent id is required"
            );
        }

        actor.attemptsTo(
                Patch.to(
                        "/api/v1/fieldwork/consents/"
                                + consentId
                                + "/withdraw"
                )
        );

        SerenityRest.lastResponse()
                .then()
                .statusCode(200);

        String status =
                SerenityRest
                        .lastResponse()
                        .jsonPath()
                        .getString(
                                "status"
                        );

        actor.remember(
                "research.consent.status",
                status
        );

        actor.remember(
                "research.consent.withdrawn",
                "WITHDRAWN".equalsIgnoreCase(
                        status
                )
        );
    }
}