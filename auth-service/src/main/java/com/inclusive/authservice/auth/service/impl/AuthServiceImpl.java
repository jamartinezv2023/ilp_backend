// Location: auth-service/src/main/java/com/inclusive/authservice/auth/service/impl/AuthServiceImpl.java
package com.inclusive.authservice.auth.service.impl;

import com.inclusive.authservice.auth.dto.LoginRequest;
import com.inclusive.authservice.auth.dto.LoginResponse;
import com.inclusive.authservice.auth.service.AuthService;
import com.inclusive.authservice.auth.service.RefreshTokenService;
import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.repository.authorization.UserAccountRepository;
import com.inclusive.authservice.repository.authorization.UserRoleRepository;
import com.inclusive.authservice.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserAccountRepository userAccountRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            UserAccountRepository userAccountRepository,
            UserRoleRepository userRoleRepository,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            PasswordEncoder passwordEncoder
    ) {
        this.userAccountRepository = userAccountRepository;
        this.userRoleRepository = userRoleRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(String tenantId, LoginRequest request) {

        UserAccount user = userAccountRepository
                .findByTenantIdAndEmail(tenantId, request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        UUID tenantUUID = UUID.fromString(tenantId);

        List<UUID> roleIds =
                userRoleRepository.findActiveRoleIdsByUserAndTenant(
                        user.getId(),
                        tenantUUID
                );

        String accessToken =
                jwtService.generateToken(user, tenantUUID, roleIds);

        String refreshToken =
                refreshTokenService.createRefreshToken(user, tenantUUID);

        return new LoginResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationSeconds()
        );
    }
}
