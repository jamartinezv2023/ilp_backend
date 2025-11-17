package com.inclusive.authservice.service;

import com.inclusive.authservice.model.User;

import java.util.List;

public interface AuthService {

    User registerUser(String email, String password);

    List<User> listUsers();
}
