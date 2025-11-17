package com.inclusive.authservice.controller;

import com.inclusive.authservice.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        return ResponseEntity.ok(authService.login(username, password));
    }

    @PostMapping("/login-2fa")
    public ResponseEntity<?> verify2FA(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String code = payload.get("code");
        return ResponseEntity.ok(authService.verify2FA(username, code));
    }
}




