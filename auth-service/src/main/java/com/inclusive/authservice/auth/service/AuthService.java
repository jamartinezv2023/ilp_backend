package com.inclusive.authservice.auth.service;

import com.inclusive.authservice.auth.dto.LoginRequest;
import com.inclusive.authservice.auth.dto.LoginResponse;

import java.util.UUID;

public interface AuthService {

    LoginResponse login(LoginRequest request, UUID tenantId);
}
