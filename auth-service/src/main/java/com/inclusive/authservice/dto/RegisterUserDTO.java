package com.inclusive.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterUserDTO {
    @NotBlank
    private String username;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;

    // Opcional: telÃ©fono para SMS MFA
    private String phoneNumber;
}



