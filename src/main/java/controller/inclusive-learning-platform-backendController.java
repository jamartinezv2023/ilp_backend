package com.inclusive.inclusive-learning-platform-backend.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inclusive-learning-platform-backend")
public class inclusive-learning-platform-backendController {

    @GetMapping
    public List<String> findAll() {
        return List.of("Sample data for inclusive-learning-platform-backend");
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id) {
        return "Sample inclusive-learning-platform-backend #" + id;
    }

    @PostMapping
    public String create(@RequestBody String body) {
        return "Created inclusive-learning-platform-backend: " + body;
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody String body) {
        return "Updated inclusive-learning-platform-backend #" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return "Deleted inclusive-learning-platform-backend #" + id;
    }
}




