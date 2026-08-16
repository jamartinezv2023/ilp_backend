package com.inclusive.adaptiveeducationservice.fieldwork.application.researchidentity;

import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectId;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectIdentity;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchConsentEligibilityQueryPort;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchSubjectIdentityPersistencePort;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchSubjectIdentityQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResearchIdentityAssignmentServiceTest {

    private FakeConsentEligibilityQueryPort consentPort;
    private FakeIdentityQueryPort queryPort;
    private FakeIdentityPersistencePort persistencePort;

    private ResearchIdentityAssignmentService service;

    @BeforeEach
    void setUp() {
        consentPort =
                new FakeConsentEligibilityQueryPort();

        queryPort =
                new FakeIdentityQueryPort();

        persistencePort =
                new FakeIdentityPersistencePort();

        service =
                new ResearchIdentityAssignmentService(
                        consentPort,
                        queryPort,
                        persistencePort
                );
    }

    @Test
    void assignsIdentityWhenConsentIsActive() {
        UUID participantUuid =
                UUID.randomUUID();

        consentPort.activeConsent = true;

        ResearchSubjectIdentity result =
                service.assign(participantUuid);

        assertEquals(
                participantUuid,
                result.getParticipantUuid()
        );

        assertTrue(result.isActive());

        assertEquals(
                1,
                persistencePort.saveCount
        );

        assertSame(
                persistencePort.savedIdentity,
                result
        );
    }

    @Test
    void reusesExistingActiveIdentity() {
        UUID participantUuid =
                UUID.randomUUID();

        ResearchSubjectIdentity existing =
                new ResearchSubjectIdentity(
                        participantUuid,
                        ResearchSubjectId.generate()
                );

        queryPort.existing =
                Optional.of(existing);

        ResearchSubjectIdentity result =
                service.assign(participantUuid);

        assertSame(
                existing,
                result
        );
    }

    @Test
    void rejectsAssignmentWithoutActiveConsent() {
        UUID participantUuid =
                UUID.randomUUID();

        consentPort.activeConsent = false;

        assertThrows(
                ResearchConsentRequiredException.class,
                () -> service.assign(participantUuid)
        );

        assertEquals(
                0,
                persistencePort.saveCount
        );
    }

    @Test
    void rejectsNullParticipantUuid() {
        assertThrows(
                NullPointerException.class,
                () -> service.assign(null)
        );

        assertEquals(
                0,
                persistencePort.saveCount
        );
    }

    @Test
    void doesNotPersistWhenExistingIdentityIsReused() {
        UUID participantUuid =
                UUID.randomUUID();

        ResearchSubjectIdentity existing =
                new ResearchSubjectIdentity(
                        participantUuid,
                        ResearchSubjectId.generate()
                );

        queryPort.existing =
                Optional.of(existing);

        service.assign(participantUuid);

        assertEquals(
                0,
                persistencePort.saveCount
        );
    }

    private static final class
    FakeConsentEligibilityQueryPort
            implements ResearchConsentEligibilityQueryPort {

        private boolean activeConsent;

        @Override
        public boolean hasActiveConsent(
                UUID participantUuid
        ) {
            return activeConsent;
        }
    }

    private static final class
    FakeIdentityQueryPort
            implements ResearchSubjectIdentityQueryPort {

        private Optional<ResearchSubjectIdentity> existing =
                Optional.empty();

        @Override
        public Optional<ResearchSubjectIdentity>
        findActiveByParticipantUuid(
                UUID participantUuid
        ) {
            return existing;
        }
    }

    private static final class
    FakeIdentityPersistencePort
            implements ResearchSubjectIdentityPersistencePort {

        private int saveCount;

        private ResearchSubjectIdentity savedIdentity;

        @Override
        public ResearchSubjectIdentity save(
                ResearchSubjectIdentity identity
        ) {
            saveCount++;

            savedIdentity =
                    identity;

            return identity;
        }
    }
}
