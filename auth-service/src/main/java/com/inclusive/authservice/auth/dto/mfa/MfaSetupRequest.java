package com.inclusive.authservice.auth.dto.mfa;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MfaSetupRequest(
        @Email
        @NotBlank
        String email
) {
}
