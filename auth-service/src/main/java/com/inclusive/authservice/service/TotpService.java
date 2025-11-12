package com.inclusive.authservice.service;

import com.inclusive.authservice.dto.mfa.TotpSetupResponse;
import com.inclusive.authservice.model.User;

public interface TotpService {
    TotpSetupResponse enable(User user);                 // genera secreto + otpauth (pendiente de verificar)
    boolean verify(User user, String code);              // verifica y (si estaba pendiente) marca enabled
    boolean disable(User user, String currentCode);      // verifica cÃ³digo y deshabilita
    TotpSetupResponse rotate(User user, String currentCode); // verifica y rota secreto devolviendo nuevo QR
}



