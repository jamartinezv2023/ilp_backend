package com.inclusive.authservice.auth.service.impl;

import com.inclusive.authservice.auth.dto.LoginRequest;
import com.inclusive.authservice.auth.dto.LoginResponse;
import com.inclusive.authservice.auth.service.AuthService;
import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.repository.authorization.UserAccountRepository;
import com.inclusive.authservice.security.jwt.JwtService;
import com.inclusive.authservice.security.mfa.MfaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MfaService mfaService;

    @Override
    public LoginResponse login(LoginRequest request, UUID tenantId) {

        UserAccount user = userAccountRepository
                .findByEmailAndTenantId(request.email(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (user.isMfaEnabled()) {
            if (request.mfaCode() == null) {
                return LoginResponse.requiresMfa();
            }

            boolean validMfaCode = mfaService.verifyCode(
                    user.getMfaSecret(),
                    request.mfaCode()
            );

            if (!validMfaCode) {
                throw new IllegalArgumentException("Invalid MFA code");
            }
        }

        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                tenantId,
                user.getEmail(),
                Set.of("USER"),
                Set.of()
        );

        String refreshToken = jwtService.generateRefreshToken();

        return LoginResponse.authenticated(accessToken, refreshToken);
    }
}
