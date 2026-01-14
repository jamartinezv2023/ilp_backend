package com.inclusive.authservice.auth.controller;

import com.inclusive.authservice.auth.dto.LoginRequest;
import com.inclusive.authservice.auth.dto.LoginResponse;
import com.inclusive.authservice.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request, tenantId);
    }
}


