package com.inclusive.authservice.security.jwt;

import com.inclusive.authservice.entity.UserAccount;

import java.util.List;
import java.util.UUID;

public interface JwtService {

    String generateToken(
            UserAccount user,
            UUID tenantId,
            List<UUID> roleIds
    );

    long getAccessTokenExpirationSeconds();
}
