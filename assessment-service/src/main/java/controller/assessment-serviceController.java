package com.inclusive.assessment-service.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/assessment-service")
public class assessment-serviceController {

    @GetMapping
    public List<String> findAll() {
        return List.of("Sample data for assessment-service");
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id) {
        return "Sample assessment-service #" + id;
    }

    @PostMapping
    public String create(@RequestBody String body) {
        return "Created assessment-service: " + body;
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody String body) {
        return "Updated assessment-service #" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return "Deleted assessment-service #" + id;
    }
}




