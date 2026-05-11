package com.inclusive.bdd.models.authentication;

public record LoginCredentials(
        String email,
        String password,
        String tenantId
) {
}
