package com.inclusive.authservice.hardening;

import com.inclusive.authservice.auth.controller.MfaController;
import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.repository.authorization.UserAccountRepository;
import com.inclusive.authservice.security.SecurityConfig;
import com.inclusive.authservice.security.jwt.JwtAuthConverter;
import com.inclusive.authservice.security.jwt.JwtAuthoritiesExtractor;
import com.inclusive.authservice.security.mfa.MfaService;
import com.inclusive.authservice.security.tenant.TenantValidationFilter;
import com.inclusive.authservice.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MfaSecurityChainHardeningTest.TestConfiguration.class)
@WebAppConfiguration
class MfaSecurityChainHardeningTest {
    private static final UUID TENANT = UUID.fromString("11111111-1111-4111-8111-111111111111");
    private static final UUID USER = UUID.fromString("22222222-2222-4222-8222-222222222222");
    @Autowired private WebApplicationContext context;
    @Autowired private UserAccountRepository repository;
    @Autowired private MfaService mfa;
    @Autowired private JwtDecoder decoder;
    @Autowired private FilterRegistrationBean<TenantValidationFilter> registration;
    private MockMvc mvc;
    private UserAccount user;

    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({SecurityConfig.class, TenantValidationFilter.class, JwtAuthConverter.class,
            JwtAuthoritiesExtractor.class, MfaController.class})
    static class TestConfiguration {
        @Bean UserAccountRepository repository() { return mock(UserAccountRepository.class); }
        @Bean MfaService mfaService() { return mock(MfaService.class); }
        @Bean JwtDecoder jwtDecoder() { return mock(JwtDecoder.class); }
    }

    @BeforeEach
    void arrange() {
        reset(repository, mfa, decoder);
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        user = account(true, false, null);
        when(repository.findForMfaEnrollment(USER, TENANT)).thenAnswer(invocation -> Optional.of(user));
        when(mfa.generateSecret()).thenReturn("fixture-enrollment-secret");
        when(mfa.buildQrProvisioningUri(anyString(), anyString())).thenReturn("otpauth://fixture-only");
        when(decoder.decode("fixture-valid-token")).thenReturn(token(TENANT.toString()));
    }

    @AfterEach
    void clear() { TenantContext.clear(); }

    private UserAccount account(boolean enabled, boolean mfaEnabled, String secret) {
        return new UserAccount(USER, TENANT, "user@example.invalid", "fixture-hash", enabled,
                mfaEnabled, secret, Instant.now(), null);
    }

    private Jwt token(String tenant) {
        Jwt.Builder builder = Jwt.withTokenValue("fixture-valid-token").header("alg", "RS256")
                .subject(USER.toString()).issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300));
        if (tenant != null) { builder.claim("tenantId", tenant); }
        return builder.build();
    }

    private MockHttpServletRequestBuilder request(String operation, String email, boolean authenticated) {
        MockHttpServletRequestBuilder builder = post("/auth/mfa/" + operation)
                .header("X-Tenant-Id", TENANT.toString()).contentType("application/json")
                .content("{\"email\":\"" + email + "\",\"code\":123456}");
        if (authenticated) { builder.header("Authorization", "Bearer fixture-valid-token"); }
        return builder;
    }

    @Test
    void anonymousCannotEnrollOrVerify() throws Exception {
        mvc.perform(request("setup", "user@example.invalid", false)).andExpect(status().isUnauthorized());
        mvc.perform(request("verify", "user@example.invalid", false)).andExpect(status().isUnauthorized());
        verifyNoInteractions(repository, mfa);
    }

    @Test
    void invalidBearerIsRejectedBeforeController() throws Exception {
        when(decoder.decode("fixture-valid-token")).thenThrow(new BadJwtException("Invalid fixture"));
        mvc.perform(request("setup", "user@example.invalid", true)).andExpect(status().isUnauthorized());
        verifyNoInteractions(repository, mfa);
    }

    @Test
    void bearerTenantMismatchCannotReachController() throws Exception {
        when(decoder.decode("fixture-valid-token")).thenReturn(token(UUID.randomUUID().toString()));
        mvc.perform(request("setup", "user@example.invalid", true))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.error").value("TENANT_MISMATCH"));
        verifyNoInteractions(repository, mfa);
        assertNull(TenantContext.getTenantId());
    }

    @Test
    void missingTenantClaimCannotReachController() throws Exception {
        when(decoder.decode("fixture-valid-token")).thenReturn(token(null));
        mvc.perform(request("setup", "user@example.invalid", true)).andExpect(status().isUnauthorized());
        verifyNoInteractions(repository, mfa);
    }

    @Test
    void cannotSelectAnotherAccountByEmail() throws Exception {
        mvc.perform(request("setup", "other@example.invalid", true)).andExpect(status().isForbidden());
        mvc.perform(request("verify", "other@example.invalid", true)).andExpect(status().isForbidden());
        verify(repository, never()).findByEmailAndTenantId(anyString(), any());
        verify(repository, never()).save(any());
        verifyNoInteractions(mfa);
    }

    @Test
    void disabledAccountCannotEnroll() throws Exception {
        user = account(false, false, null);
        mvc.perform(request("setup", "user@example.invalid", true)).andExpect(status().isForbidden());
        verifyNoInteractions(mfa);
        verify(repository, never()).save(any());
    }

    @Test
    void enabledMfaCannotBeReset() throws Exception {
        user = account(true, true, "existing-fixture-secret");
        mvc.perform(request("setup", "user@example.invalid", true)).andExpect(status().isConflict());
        assertTrue(user.isMfaEnabled());
        assertEquals("existing-fixture-secret", user.getMfaSecret());
        verifyNoInteractions(mfa);
        verify(repository, never()).save(any());
    }

    @Test
    void pendingSecretCannotBeReplaced() throws Exception {
        user = account(true, false, "pending-fixture-secret");
        mvc.perform(request("setup", "user@example.invalid", true)).andExpect(status().isConflict());
        assertEquals("pending-fixture-secret", user.getMfaSecret());
        verifyNoInteractions(mfa);
    }

    @Test
    void ownerCanStartEnrollmentWithoutEnablingMfa() throws Exception {
        mvc.perform(request("setup", "user@example.invalid", true)).andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.qrProvisioningUri").value("otpauth://fixture-only"));
        assertFalse(user.isMfaEnabled());
        assertEquals("fixture-enrollment-secret", user.getMfaSecret());
        verify(repository).findForMfaEnrollment(USER, TENANT);
        verify(repository).save(user);
        assertNull(TenantContext.getTenantId());
    }

    @Test
    void invalidVerificationDoesNotEnableMfa() throws Exception {
        user = account(true, false, "pending-fixture-secret");
        when(mfa.verifyCode(anyString(), anyInt())).thenReturn(false);
        mvc.perform(request("verify", "user@example.invalid", true)).andExpect(status().isOk())
                .andExpect(content().string("false"));
        assertFalse(user.isMfaEnabled());
        verify(repository, never()).save(any());
    }

    @Test
    void validOwnerVerificationEnablesPendingMfa() throws Exception {
        user = account(true, false, "pending-fixture-secret");
        when(mfa.verifyCode("pending-fixture-secret", 123456)).thenReturn(true);
        mvc.perform(request("verify", "user@example.invalid", true)).andExpect(status().isOk())
                .andExpect(content().string("true"));
        assertTrue(user.isMfaEnabled());
        verify(repository).save(user);
    }

    @Test
    void servletAutoRegistrationIsDisabled() {
        assertFalse(registration.isEnabled());
    }
}
