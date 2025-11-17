package com.inclusive.authservice.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TotpSetupResponse {
    private String secret;     // Base32
    private String otpauthUrl; // otpauth:// URI
    private String qrImage;    // data:image/png;base64,....
}



