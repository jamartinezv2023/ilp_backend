package com.inclusive.notification-service.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notification-service")
public class notification-serviceController {

    @GetMapping
    public List<String> findAll() {
        return List.of("Sample data for notification-service");
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id) {
        return "Sample notification-service #" + id;
    }

    @PostMapping
    public String create(@RequestBody String body) {
        return "Created notification-service: " + body;
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody String body) {
        return "Updated notification-service #" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return "Deleted notification-service #" + id;
    }
}




