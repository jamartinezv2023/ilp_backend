package com.inclusive.authservice.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        boolean mfaRequired
) {
    public static LoginResponse requiresMfa() {
        return new LoginResponse(null, null, true);
    }

    public static LoginResponse authenticated(
            String accessToken,
            String refreshToken
    ) {
        return new LoginResponse(accessToken, refreshToken, false);
    }
}
