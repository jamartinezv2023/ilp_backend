package com.inclusive.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Login2FARequest {

    @NotBlank(message = "El nombre de usuario o email es obligatorio")
    private String usernameOrEmail;

    @NotBlank(message = "El cÃ³digo de verificaciÃ³n es obligatorio")
    private String verificationCode;
}



