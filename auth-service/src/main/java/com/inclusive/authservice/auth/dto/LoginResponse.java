package com.inclusive.authservice.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
