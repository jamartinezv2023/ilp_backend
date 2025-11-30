package com.inclusive.authservice.dto;

import com.inclusive.authservice.model.User;

public class AuthResponse {

    private Long id;
    private String email;
    private boolean enabled;
    private AuthTokens tokens;

    public static AuthResponse of(User user, AuthTokens tokens) {
        AuthResponse r = new AuthResponse();
        r.id = user.getId();
        r.email = user.getEmail();
        r.enabled = user.isEnabled();
        r.tokens = tokens;
        return r;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public boolean isEnabled() { return enabled; }
    public AuthTokens getTokens() { return tokens; }
}
