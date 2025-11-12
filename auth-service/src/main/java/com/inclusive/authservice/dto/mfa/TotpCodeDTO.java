package com.inclusive.authservice.dto.mfa;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TotpCodeDTO {
    @NotBlank
    private String code;
}



