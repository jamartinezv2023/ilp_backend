package com.inclusive.authservice.controller.authorization;

import com.inclusive.authservice.entity.authorization.Permission;
import com.inclusive.authservice.service.authorization.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    public ResponseEntity<Permission> create(
            @RequestParam String code,
            @RequestParam(required = false) String description
    ) {
        return ResponseEntity.ok(
                permissionService.createPermission(code, description)
        );
    }

    @GetMapping
    public ResponseEntity<List<Permission>> findAll() {
        return ResponseEntity.ok(
                permissionService.getAllPermissions()
        );
    }
}