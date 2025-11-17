package com.inclusive.authservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    @GetMapping
    public String getPermissions() {
        return "Permissions list";
    }
}
