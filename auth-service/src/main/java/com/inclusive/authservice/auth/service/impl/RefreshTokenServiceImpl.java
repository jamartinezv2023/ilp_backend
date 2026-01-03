// Location: auth-service/src/main/java/com/inclusive/authservice/auth/service/impl/RefreshTokenServiceImpl.java
package com.inclusive.authservice.auth.service.impl;

import com.inclusive.authservice.auth.service.RefreshTokenService;
import com.inclusive.authservice.entity.RefreshToken;
import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.repository.authorization.RefreshTokenRepository;
import com.inclusive.authservice.security.jwt.JwtProperties;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties props;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            JwtProperties props
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.props = props;
    }

    @Override
    public String createRefreshToken(UserAccount user, UUID tenantId) {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setId(UUID.randomUUID());
        refreshToken.setUserId(user.getId());
        refreshToken.setTenantId(tenantId);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setRevoked(false);
        refreshToken.setCreatedAt(OffsetDateTime.now());

        // ✅ MÉTODO CORRECTO
        refreshToken.setExpiresAt(
                OffsetDateTime.now()
                        .plusSeconds(props.getRefreshTokenExpirationSeconds())
        );

        refreshTokenRepository.save(refreshToken);

        return refreshToken.getToken();
    }
}
