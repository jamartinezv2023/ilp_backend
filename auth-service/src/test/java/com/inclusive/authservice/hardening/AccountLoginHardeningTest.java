package com.inclusive.authservice.hardening;

import com.inclusive.authservice.auth.dto.LoginRequest;
import com.inclusive.authservice.auth.dto.LoginResponse;
import com.inclusive.authservice.auth.service.impl.AuthServiceImpl;
import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.repository.authorization.UserAccountRepository;
import com.inclusive.authservice.security.jwt.JwtService;
import com.inclusive.authservice.security.mfa.MfaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountLoginHardeningTest {
    private final UUID tenant = UUID.fromString("11111111-1111-4111-8111-111111111111");
    private final UserAccountRepository repository = mock(UserAccountRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final JwtService jwt = mock(JwtService.class);
    private final MfaService mfa = mock(MfaService.class);
    private final AuthServiceImpl service = new AuthServiceImpl(repository, encoder, jwt, mfa);

    @BeforeEach
    void arrange() {
        when(encoder.matches("fixture-password", "fixture-hash")).thenReturn(true);
    }

    private void account(boolean enabled, boolean mfaEnabled, String secret) {
        UserAccount user = new UserAccount(UUID.randomUUID(), tenant, "user@example.invalid",
                "fixture-hash", enabled, mfaEnabled, secret, Instant.now(), null);
        when(repository.findByEmailAndTenantId("user@example.invalid", tenant))
                .thenReturn(Optional.of(user));
    }

    private LoginRequest request(Integer code) {
        return new LoginRequest("user@example.invalid", "fixture-password", code);
    }

    @Test
    void disabledAccountNeverReceivesTokens() {
        account(false, false, null);
        assertThrows(BadCredentialsException.class, () -> service.login(request(null), tenant));
        verifyNoInteractions(jwt, mfa);
    }

    @Test
    void disabledAccountWithMfaNeverReceivesChallengeOrTokens() {
        account(false, true, "fixture-secret");
        assertThrows(BadCredentialsException.class, () -> service.login(request(123456), tenant));
        verifyNoInteractions(jwt, mfa);
    }

    @Test
    void unknownAccountIsRejected() {
        when(repository.findByEmailAndTenantId(anyString(), any())).thenReturn(Optional.empty());
        assertThrows(BadCredentialsException.class, () -> service.login(request(null), tenant));
        verifyNoInteractions(jwt, mfa);
    }

    @Test
    void wrongPasswordIsRejected() {
        account(true, false, null);
        when(encoder.matches(any(), any())).thenReturn(false);
        assertThrows(BadCredentialsException.class, () -> service.login(request(null), tenant));
        verifyNoInteractions(jwt, mfa);
    }

    @Test
    void mfaPendingDoesNotIssueTokens() {
        account(true, true, "fixture-secret");
        LoginResponse response = service.login(request(null), tenant);
        assertTrue(response.mfaRequired());
        assertNull(response.accessToken());
        assertNull(response.refreshToken());
        verifyNoInteractions(jwt, mfa);
    }

    @Test
    void invalidMfaIsRejected() {
        account(true, true, "fixture-secret");
        when(mfa.verifyCode("fixture-secret", 123456)).thenReturn(false);
        assertThrows(BadCredentialsException.class, () -> service.login(request(123456), tenant));
        verifyNoInteractions(jwt);
    }

    @Test
    void outOfRangeMfaIsRejectedBeforeVerification() {
        account(true, true, "fixture-secret");
        assertThrows(BadCredentialsException.class, () -> service.login(request(-1), tenant));
        assertThrows(BadCredentialsException.class, () -> service.login(request(1000000), tenant));
        verifyNoInteractions(jwt, mfa);
    }

    @Test
    void enabledAccountKeepsExistingTokenFlow() {
        account(true, false, null);
        when(jwt.generateAccessToken(any(), eq(tenant), anyString(), anySet(), anySet()))
                .thenReturn("fixture-access");
        when(jwt.generateRefreshToken()).thenReturn("fixture-refresh");
        LoginResponse response = service.login(request(null), tenant);
        assertFalse(response.mfaRequired());
        assertEquals("fixture-access", response.accessToken());
    }
}
