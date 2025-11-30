// src/main/java/com/inclusive/authservice/service/AuthService.java
package com.inclusive.authservice.service;

import com.inclusive.authservice.model.User;
import com.inclusive.authservice.security.AuthTokens;

import java.util.List;

public interface AuthService {

    User registerUser(String email, String password);

    List<User> listUsers();

    AuthTokens login(String email, String password);

    AuthTokens refresh(String refreshToken);
}
