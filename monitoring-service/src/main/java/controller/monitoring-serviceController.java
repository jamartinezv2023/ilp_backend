package com.inclusive.monitoring-service.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/monitoring-service")
public class monitoring-serviceController {

    @GetMapping
    public List<String> findAll() {
        return List.of("Sample data for monitoring-service");
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id) {
        return "Sample monitoring-service #" + id;
    }

    @PostMapping
    public String create(@RequestBody String body) {
        return "Created monitoring-service: " + body;
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody String body) {
        return "Updated monitoring-service #" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return "Deleted monitoring-service #" + id;
    }
}




