package com.inclusive.authservice.service;

import com.inclusive.authservice.model.User;

public interface VerificationService {
    String generateAndSend(User user, String channel, String destination);
    boolean verifyToken(String token);
}



