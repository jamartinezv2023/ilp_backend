package com.inclusive.authservice.auth.controller;

import com.inclusive.authservice.auth.dto.LoginRequest;
import com.inclusive.authservice.auth.dto.LoginResponse;
import com.inclusive.authservice.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/{tenantId}/login")
    public ResponseEntity<LoginResponse> login(
            @PathVariable String tenantId,
            @RequestBody @Valid LoginRequest request
    ) {
        return ResponseEntity.ok(
                authService.login(tenantId, request)
        );
    }
}


