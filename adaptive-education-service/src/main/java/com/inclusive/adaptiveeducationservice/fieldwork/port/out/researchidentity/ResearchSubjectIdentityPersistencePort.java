package com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity;

import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectIdentity;

@FunctionalInterface
public interface ResearchSubjectIdentityPersistencePort {

    ResearchSubjectIdentity save(
            ResearchSubjectIdentity identity
    );
}
