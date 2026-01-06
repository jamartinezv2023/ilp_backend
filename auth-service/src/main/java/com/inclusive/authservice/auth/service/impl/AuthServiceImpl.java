// Location: auth-service/src/main/java/com/inclusive/authservice/auth/service/impl/AuthServiceImpl.java
package com.inclusive.authservice.auth.service.impl;

import com.inclusive.authservice.auth.dto.LoginRequest;
import com.inclusive.authservice.auth.dto.LoginResponse;
import com.inclusive.authservice.auth.service.AuthService;
import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.repository.authorization.UserAccountRepository;
import com.inclusive.authservice.security.jwt.JwtService;
import com.inclusive.authservice.service.authorization.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public LoginResponse login(String tenantId, LoginRequest request) {

        // 🔑 Conversión controlada y explícita
        UUID tenantUUID = UUID.fromString(tenantId);

        UserAccount user = userAccountRepository
                .findByEmailAndTenantId(request.getEmail(), tenantUUID)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(user, tenantUUID);
        String refreshToken = refreshTokenService.create(user);

        return new LoginResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationSeconds()
        );
    }
}
