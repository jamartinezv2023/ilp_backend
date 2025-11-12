package com.inclusive.common_DUPLICATE_DISABLED.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/common_DUPLICATE_DISABLED")
public class common_DUPLICATE_DISABLEDController {

    @GetMapping
    public List<String> findAll() {
        return List.of("Sample data for common_DUPLICATE_DISABLED");
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id) {
        return "Sample common_DUPLICATE_DISABLED #" + id;
    }

    @PostMapping
    public String create(@RequestBody String body) {
        return "Created common_DUPLICATE_DISABLED: " + body;
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody String body) {
        return "Updated common_DUPLICATE_DISABLED #" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return "Deleted common_DUPLICATE_DISABLED #" + id;
    }
}




