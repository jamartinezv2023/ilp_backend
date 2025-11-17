package com.inclusive.authservice.controller;

import com.inclusive.authservice.model.User;
import com.inclusive.authservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        User user = authService.registerUser(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return authService.listUsers().stream()
                .map(UserResponse::from)
                .toList();
    }

    // =====================
    // DTOs internos
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
}
