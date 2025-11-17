package com.inclusive.authservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/verification")
public class VerificationTokenController {

    @GetMapping("/{token}")
    public String verify(@PathVariable String token) {
        return "Token received: " + token;
    }
}
