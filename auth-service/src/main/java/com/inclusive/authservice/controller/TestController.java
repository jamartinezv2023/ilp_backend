package com.inclusive.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/user/test")
    public String user() {
        return "USER OK";
    }

    @GetMapping("/api/admin/test")
    public String admin() {
        return "ADMIN OK";
    }
}
