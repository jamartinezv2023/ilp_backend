package com.inclusive.adaptiveeducationservice.fieldwork.application.researchidentity;

import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchConsentEligibilityQueryPort;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchSubjectIdentityPersistencePort;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchSubjectIdentityQueryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ResearchIdentityRealSpringWiringTest {

    @Autowired
    private ResearchIdentityAssignmentService
            assignmentService;

    @Autowired
    private ResearchConsentEligibilityQueryPort
            consentEligibilityQueryPort;

    @Autowired
    private ResearchSubjectIdentityQueryPort
            identityQueryPort;

    @Autowired
    private ResearchSubjectIdentityPersistencePort
            identityPersistencePort;

    @Test
    void wiresResearchIdentityUseCaseWithProductionAdapters() {
        assertThat(assignmentService)
                .isNotNull();

        assertThat(consentEligibilityQueryPort)
                .isNotNull();

        assertThat(identityQueryPort)
                .isNotNull();

        assertThat(identityPersistencePort)
                .isNotNull();
    }
}
