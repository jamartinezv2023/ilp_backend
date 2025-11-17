package com.inclusive.authservice.dto.mfa;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TotpSetupResponse {
    private String secretBase32;
    private String otpauthUri;
    private String qrUrl; // listo para usar en el cliente (opcional)
}



