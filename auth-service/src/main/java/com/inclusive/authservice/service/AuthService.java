package com.inclusive.authservice.service;

import com.inclusive.authservice.dto.RegisterUserDTO;
import java.util.Map;

public interface AuthService {
    Map<String,Object> register(RegisterUserDTO dto);
    Map<String,Object> verifyEmail(String token);
    Map<String,Object> login(String usernameOrEmail, String password);
    Map<String,Object> resendVerification(String usernameOrEmail);
    Map<String,Object> verifyLogin2FA(String usernameOrEmail, String code);
}



