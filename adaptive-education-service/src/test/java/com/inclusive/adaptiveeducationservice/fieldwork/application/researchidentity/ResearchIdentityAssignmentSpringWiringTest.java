package com.inclusive.adaptiveeducationservice.fieldwork.application.researchidentity;

import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchConsentEligibilityQueryPort;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchSubjectIdentityPersistencePort;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchSubjectIdentityQueryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ResearchIdentityAssignmentSpringWiringTest {

    @MockBean
    private ResearchConsentEligibilityQueryPort
            consentEligibilityQueryPort;

    @MockBean
    private ResearchSubjectIdentityQueryPort
            identityQueryPort;

    @MockBean
    private ResearchSubjectIdentityPersistencePort
            identityPersistencePort;

    @Autowired(required = false)
    private ResearchIdentityAssignmentService
            researchIdentityAssignmentService;

    @Test
    void exposesResearchIdentityAssignmentServiceAsSpringBean() {
        assertThat(researchIdentityAssignmentService)
                .isNotNull();
    }
}
