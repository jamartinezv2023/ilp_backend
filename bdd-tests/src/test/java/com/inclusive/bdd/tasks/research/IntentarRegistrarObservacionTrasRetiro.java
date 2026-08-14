package com.inclusive.bdd.tasks.research;

import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.UUID;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class IntentarRegistrarObservacionTrasRetiro
        implements Task {

    public static IntentarRegistrarObservacionTrasRetiro
    delParticipante() {

        return instrumented(
                IntentarRegistrarObservacionTrasRetiro.class
        );
    }

    @Override
    public <T extends Actor> void performAs(
            T actor
    ) {

        String researchParticipantUuid =
                actor.recall(
                        "research.participant.uuid"
                );

        if (
                researchParticipantUuid == null
                        || researchParticipantUuid.isBlank()
        ) {
            throw new IllegalStateException(
                    "research participant UUID is required"
            );
        }

        /*
         * SubmitAssessmentService validates the educational
         * participant before validating research consent.
         *
         * Therefore this BDD scenario creates a valid,
         * isolated student profile before exercising the
         * withdrawn-consent gate.
         */
        actor.attemptsTo(
                Post.to(
                        "/api/v1/students"
                ).with(
                        request ->
                                request
                                        .contentType(
                                                "application/json"
                                        )
                                        .body(
                                                """
                                                {
                                                  "fullName": "BDD Research Participant",
                                                  "grade": "10A",
                                                  "age": 16,
                                                  "learningProfile": "BDD scientific observation",
                                                  "vocationalInterest": "Research",
                                                  "supportLevel": "LOW",
                                                  "inclusiveStrategies": [
                                                    "BDD controlled strategy"
                                                  ],
                                                  "pedagogicalRecommendations": [
                                                    "BDD controlled recommendation"
                                                  ]
                                                }
                                                """
                                        )
                )
        );

        Response studentResponse =
                SerenityRest.lastResponse();

        if (studentResponse.statusCode() != 200) {
            throw new IllegalStateException(
                    "student fixture creation failed with HTTP "
                            + studentResponse.statusCode()
                            + ": "
                            + studentResponse
                            .getBody()
                            .asString()
            );
        }

        String studentId =
                studentResponse
                        .jsonPath()
                        .getString(
                                "id"
                        );

        if (
                studentId == null
                        || studentId.isBlank()
        ) {
            throw new IllegalStateException(
                    "BDD student id is required"
            );
        }

        String administrationId =
                "BDD-WITHDRAW-"
                        + UUID.randomUUID();

        actor.remember(
                "research.observation.student-id",
                studentId
        );

        actor.remember(
                "research.observation.administration-id",
                administrationId
        );

        String requestBody =
                """
                {
                  "administrationId": "__ADMINISTRATION_ID__",
                  "participantId": "__STUDENT_ID__",
                  "researchParticipantUuid": "__RESEARCH_PARTICIPANT_UUID__",
                  "assessmentCode": "BDD_CONSENT_GATE",
                  "assessmentVersion": "1.0",
                  "responses": [
                    {
                      "questionCode": "BDD-Q1",
                      "selectedOptionIds": [],
                      "rankings": {},
                      "numericValue": 1.0,
                      "textValue": "consent-withdrawal-gate"
                    }
                  ],
                  "context": {
                    "source": "BDD_CONSENT_WITHDRAWAL"
                  }
                }
                """.replace(
                        "__ADMINISTRATION_ID__",
                        administrationId
                ).replace(
                        "__STUDENT_ID__",
                        studentId
                ).replace(
                        "__RESEARCH_PARTICIPANT_UUID__",
                        researchParticipantUuid
                );

        actor.attemptsTo(
                Post.to(
                        "/api/v1/assessment-submissions"
                ).with(
                        request ->
                                request
                                        .contentType(
                                                "application/json"
                                        )
                                        .body(
                                                requestBody
                                        )
                )
        );

        Response submissionResponse =
                SerenityRest.lastResponse();

        actor.remember(
                "research.observation.submission-status-code",
                submissionResponse.statusCode()
        );

        actor.remember(
                "research.observation.submission-response-body",
                submissionResponse
                        .getBody()
                        .asString()
        );

        /*
         * Independent persistence verification.
         *
         * A rejected submission must not create a scientific
         * observation for this administrationId.
         */
        actor.attemptsTo(
                Get.resource(
                        "/api/v1/assessment-scientific-observations/"
                                + administrationId
                )
        );

        Response observationResponse =
                SerenityRest.lastResponse();

        actor.remember(
                "research.observation.lookup-status-code",
                observationResponse.statusCode()
        );

        actor.remember(
                "research.observation.lookup-response-body",
                observationResponse
                        .getBody()
                        .asString()
        );
    }
}
