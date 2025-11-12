package com.inclusive.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TotpVerifyRequest {

    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String code; // 6 dÃ­gitos
}



