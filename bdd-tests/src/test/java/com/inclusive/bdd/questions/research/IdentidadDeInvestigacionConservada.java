package com.inclusive.bdd.questions.research;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class IdentidadDeInvestigacionConservada
        implements Question<Boolean> {

    public static IdentidadDeInvestigacionConservada
    entreIncorporaciones() {
        return new IdentidadDeInvestigacionConservada();
    }

    @Override
    public Boolean answeredBy(
            Actor actor
    ) {
        String previousResearchSubjectId =
                actor.recall(
                        "research.identity.previous-subject-id"
                );

        String currentResearchSubjectId =
                actor.recall(
                        "research.identity.subject-id"
                );

        if (
                previousResearchSubjectId == null
                        || previousResearchSubjectId.isBlank()
        ) {
            return false;
        }

        if (
                currentResearchSubjectId == null
                        || currentResearchSubjectId.isBlank()
        ) {
            return false;
        }

        return previousResearchSubjectId.equals(
                currentResearchSubjectId
        );
    }
}
