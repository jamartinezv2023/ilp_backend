package com.inclusive.authservice.auth.service;

import com.inclusive.authservice.auth.dto.LoginRequest;
import com.inclusive.authservice.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(String tenantId, LoginRequest request);
}
