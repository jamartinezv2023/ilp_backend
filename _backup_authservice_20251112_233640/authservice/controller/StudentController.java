package com.inclusive.authservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student")
public class StudentController {

    @GetMapping("/{id}")
    public String getStudent(@PathVariable Long id) {
        return "Student: " + id;
    }
}
