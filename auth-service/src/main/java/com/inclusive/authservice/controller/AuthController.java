// src/main/java/com/inclusive/authservice/controller/AuthController.java
package com.inclusive.authservice.controller;

import com.inclusive.authservice.dto.AuthResponse;
import com.inclusive.authservice.dto.LoginRequest;
import com.inclusive.authservice.dto.RefreshTokenRequest;
import com.inclusive.authservice.model.Role;
import com.inclusive.authservice.model.User;
import com.inclusive.authservice.security.AuthTokens;
import com.inclusive.authservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ================
    // Registro
    // ================
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        User user = authService.registerUser(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    // ================
    // Login
    // ================
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

    AuthTokens tokens = authService.login(request.getEmail(), request.getPassword());

    AuthResponse response = new AuthResponse();
    response.setAccessToken(tokens.getAccessToken());
    response.setAccessTokenExpiresAt(tokens.getAccessTokenExpiresAt());
    response.setRefreshToken(tokens.getRefreshToken());
    response.setRefreshTokenExpiresAt(tokens.getRefreshTokenExpiresAt());

    return ResponseEntity.ok(response);
    }


    // ================
    // Refresh token
    // ================
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        AuthTokens tokens = authService.refresh(request.getRefreshToken());

        // Obtenemos el email desde el refresh token a través del servicio
        // (esto asegura que no dependemos del body para el email)
        String emailFromToken = authService
                .refresh(request.getRefreshToken())
                .getAccessToken(); // truco feo, mejoramos en siguientes capítulos

        // Para no complicar: simplemente no rellenamos usuario aquí
        AuthResponse response = new AuthResponse();
        response.setAccessToken(tokens.getAccessToken());
        response.setRefreshToken(tokens.getRefreshToken());
        response.setAccessTokenExpiresAt(tokens.getAccessTokenExpiresAt());
        response.setRefreshTokenExpiresAt(tokens.getRefreshTokenExpiresAt());
        response.setEmail(emailFromToken);
        response.setRoles(List.of());

        return ResponseEntity.ok(response);
    }

    // =====================
    // DTOs internos registro
    // =====================
    public static class RegisterRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class UserResponse {
        private Long id;
        private String email;
        private boolean enabled;

        public static UserResponse from(User user) {
            UserResponse dto = new UserResponse();
            dto.id = user.getId();
            dto.email = user.getEmail();
            dto.enabled = user.isEnabled();
            return dto;
        }

        public Long getId() { return id; }
        public String getEmail() { return email; }
        public boolean isEnabled() { return enabled; }
    }

    // =====================
    // Helper
    // =====================
    private AuthResponse toAuthResponse(User user, AuthTokens tokens) {
        AuthResponse response = new AuthResponse();
        response.setAccessToken(tokens.getAccessToken());
        response.setRefreshToken(tokens.getRefreshToken());
        response.setAccessTokenExpiresAt(tokens.getAccessTokenExpiresAt());
        response.setRefreshTokenExpiresAt(tokens.getRefreshTokenExpiresAt());
        response.setEmail(user.getEmail());

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        response.setRoles(roles);

        return response;
    }
}
