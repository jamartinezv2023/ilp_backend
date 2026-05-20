package com.inclusive.authservice.auth.dto.mfa;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MfaVerifyRequest(
        @Email
        @NotBlank
        String email,

        @NotNull
        Integer code
) {
}
