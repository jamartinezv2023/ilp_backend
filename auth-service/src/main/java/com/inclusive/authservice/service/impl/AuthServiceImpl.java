package com.inclusive.authservice.service.impl;

import com.inclusive.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final MfaServiceImpl mfaService;
    private final JwtUtil jwtUtil = new JwtUtil();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // SimulaciÃ³n de usuarios registrados
    private final Map<String, String> users = new HashMap<>() {{
        put("valeria", passwordEncoder.encode("12345"));
    }};

    // Usuarios con MFA activado
    private final Map<String, Boolean> mfaEnabled = new HashMap<>() {{
        put("valeria", true);
    }};

    public Map<String, Object> login(String username, String password) {
        Map<String, Object> response = new HashMap<>();

        if (!users.containsKey(username)) {
            response.put("error", "Usuario no encontrado");
            return response;
        }

        if (!passwordEncoder.matches(password, users.get(username))) {
            response.put("error", "ContraseÃ±a incorrecta");
            return response;
        }

        if (mfaEnabled.getOrDefault(username, false)) {
            response.put("status", "PENDING_2FA");
            response.put("message", "Introduce el cÃ³digo TOTP generado por tu app (Google Authenticator, Authy, etc.)");
            return response;
        }

        // Si no usa MFA, se genera JWT directamente
        String token = jwtUtil.generateToken(username);
        response.put("status", "SUCCESS");
        response.put("token", token);
        return response;
    }

    public Map<String, Object> verify2FA(String username, String code) {
        Map<String, Object> response = new HashMap<>();

        boolean valid = mfaService.verifyCode(username, code);
        if (!valid) {
            response.put("error", "CÃ³digo TOTP invÃ¡lido o expirado");
            return response;
        }

        String token = jwtUtil.generateToken(username);
        response.put("status", "SUCCESS");
        response.put("token", token);
        response.put("message", "AutenticaciÃ³n completada con Ã©xito");
        return response;
    }
}




