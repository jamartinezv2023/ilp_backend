package com.inclusive.authservice.security.jwt;

import com.inclusive.authservice.security.jwt.impl.JwtServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAudienceContractTest {
    @Test
    void issuerAddsAudienceWithoutChangingInstitutionOrActor() {
        var encoder = mock(JwtEncoder.class);
        var service = new JwtServiceImpl(encoder);
        ReflectionTestUtils.setField(service, "issuer", "urn:ilp:auth-test");
        ReflectionTestUtils.setField(service, "audience", "ilp-scientific-api");
        ReflectionTestUtils.setField(service, "accessTokenMinutes", 5L);
        var actor = UUID.randomUUID();
        var tenant = UUID.randomUUID();
        var jwt = Jwt.withTokenValue("SYNTHETIC").header("alg", "RS256")
                .subject(actor.toString()).issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300)).build();
        when(encoder.encode(any())).thenReturn(jwt);
        service.generateAccessToken(actor, tenant, "synthetic@example.invalid", Set.of("USER"), Set.of());
        var capture = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(encoder).encode(capture.capture());
        assertThat(capture.getValue().getClaims().getAudience()).containsExactly("ilp-scientific-api");
        assertThat(capture.getValue().getClaims().getSubject()).isEqualTo(actor.toString());
        assertThat(capture.getValue().getClaims().getClaimAsString("tenantId")).isEqualTo(tenant.toString());
    }
}
