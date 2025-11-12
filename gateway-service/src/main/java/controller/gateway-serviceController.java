package com.inclusive.gateway-service.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gateway-service")
public class gateway-serviceController {

    @GetMapping
    public List<String> findAll() {
        return List.of("Sample data for gateway-service");
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id) {
        return "Sample gateway-service #" + id;
    }

    @PostMapping
    public String create(@RequestBody String body) {
        return "Created gateway-service: " + body;
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody String body) {
        return "Updated gateway-service #" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return "Deleted gateway-service #" + id;
    }
}




