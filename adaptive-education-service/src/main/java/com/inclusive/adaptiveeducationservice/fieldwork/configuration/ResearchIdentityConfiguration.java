package com.inclusive.adaptiveeducationservice.fieldwork.configuration;

import com.inclusive.adaptiveeducationservice.fieldwork.application.researchidentity.ResearchIdentityAssignmentService;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchConsentEligibilityQueryPort;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchSubjectIdentityPersistencePort;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchSubjectIdentityQueryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResearchIdentityConfiguration {

    @Bean
    public ResearchIdentityAssignmentService
    researchIdentityAssignmentService(
            ResearchConsentEligibilityQueryPort
                    consentEligibilityQueryPort,
            ResearchSubjectIdentityQueryPort
                    identityQueryPort,
            ResearchSubjectIdentityPersistencePort
                    identityPersistencePort
    ) {
        return new ResearchIdentityAssignmentService(
                consentEligibilityQueryPort,
                identityQueryPort,
                identityPersistencePort
        );
    }
}
