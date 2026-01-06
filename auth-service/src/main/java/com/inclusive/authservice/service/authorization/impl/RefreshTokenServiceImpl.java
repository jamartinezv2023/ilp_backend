// Location: auth-service/src/main/java/com/inclusive/authservice/service/authorization/impl/RefreshTokenServiceImpl.java
package com.inclusive.authservice.service.authorization.impl;

import com.inclusive.authservice.entity.RefreshToken;
import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.repository.authorization.RefreshTokenRepository;
import com.inclusive.authservice.service.authorization.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public String create(UserAccount userAccount) {

        RefreshToken token = new RefreshToken(
                UUID.randomUUID(),
                userAccount.getTenantId(),
                userAccount.getId(),
                UUID.randomUUID().toString(),
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(7),
                false
        );

        refreshTokenRepository.save(token);
        return token.getToken();
    }
}
