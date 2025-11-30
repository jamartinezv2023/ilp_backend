package com.inclusive.authservice.service;

import com.inclusive.authservice.dto.AuthResponse;
import com.inclusive.authservice.model.User;

import java.util.List;

public interface AuthService {

    User registerUser(String email, String rawPassword);

    AuthResponse login(String email, String rawPassword);

    List<User> listUsers();
}
