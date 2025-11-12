package com.inclusive.authservice.controller;

import com.inclusive.authservice.service.impl.MfaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MfaController {

    private final MfaServiceImpl mfaService;

    @PostMapping("/login-2fa")
    public ResponseEntity<?> verifyTOTP(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String code = payload.get("code");
        boolean valid = mfaService.verifyCode(username, code);
        if (valid) {
            return ResponseEntity.ok(Map.of("message", "âœ… CÃ³digo TOTP vÃ¡lido. AutenticaciÃ³n completada."));
        }
        return ResponseEntity.status(401).body(Map.of("error", "âŒ CÃ³digo TOTP invÃ¡lido o expirado."));
    }

    @PostMapping("/totp/enable")
    public ResponseEntity<?> enableTOTP(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        return ResponseEntity.ok(mfaService.enableTOTP(username));
    }

    @PostMapping("/totp/disable")
    public ResponseEntity<?> disableTOTP(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        mfaService.disableTOTP(username);
        return ResponseEntity.ok(Map.of("message", "2FA deshabilitado correctamente"));
    }

    @PostMapping("/totp/rotate")
    public ResponseEntity<?> rotateSecret(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        return ResponseEntity.ok(mfaService.rotateSecret(username));
    }
}




