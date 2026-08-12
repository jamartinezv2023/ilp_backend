package com.inclusive.bdd.questions.research;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class IdentidadDeInvestigacionAsignada
        implements Question<Boolean> {

    public static IdentidadDeInvestigacionAsignada alParticipante() {
        return new IdentidadDeInvestigacionAsignada();
    }

    @Override
    public Boolean answeredBy(
            Actor actor
    ) {
        Boolean assigned =
                actor.recall(
                        "research.identity.assigned"
                );

        String researchSubjectId =
                actor.recall(
                        "research.identity.subject-id"
                );

        Boolean active =
                actor.recall(
                        "research.identity.active"
                );

        return Boolean.TRUE.equals(assigned)
                && researchSubjectId != null
                && !researchSubjectId.isBlank()
                && Boolean.TRUE.equals(active);
    }
}
