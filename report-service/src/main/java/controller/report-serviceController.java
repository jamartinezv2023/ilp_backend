package com.inclusive.report-service.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/report-service")
public class report-serviceController {

    @GetMapping
    public List<String> findAll() {
        return List.of("Sample data for report-service");
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id) {
        return "Sample report-service #" + id;
    }

    @PostMapping
    public String create(@RequestBody String body) {
        return "Created report-service: " + body;
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody String body) {
        return "Updated report-service #" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return "Deleted report-service #" + id;
    }
}




