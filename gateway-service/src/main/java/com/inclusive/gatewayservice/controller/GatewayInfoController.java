package com.inclusive.gatewayservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GatewayInfoController {

    @GetMapping("/api/gateway/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> data = new HashMap<>();
        data.put("service", "gateway-service");
        data.put("status", "UP");
        data.put("description", "API gateway for Inclusive Learning Platform");
        return ResponseEntity.ok(data);
    }
}