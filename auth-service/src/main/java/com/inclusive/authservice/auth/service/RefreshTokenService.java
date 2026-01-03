package com.inclusive.authservice.auth.service;

import com.inclusive.authservice.entity.UserAccount;

import java.util.UUID;

public interface RefreshTokenService {

    String createRefreshToken(UserAccount user, UUID tenantId);
}
