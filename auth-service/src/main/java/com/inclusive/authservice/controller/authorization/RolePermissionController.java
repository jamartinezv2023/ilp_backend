package com.inclusive.authservice.controller.authorization;

import com.inclusive.authservice.entity.authorization.Permission;
import com.inclusive.authservice.entity.authorization.Role;
import com.inclusive.authservice.service.authorization.PermissionService;
import com.inclusive.authservice.service.authorization.RolePermissionService;
import com.inclusive.authservice.service.authorization.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final RolePermissionService rolePermissionService;

    @PostMapping
    public ResponseEntity<?> assign(
            @RequestParam UUID roleId,
            @RequestParam UUID permissionId
    ) {
        Role role = roleService.findById(roleId);
        Permission permission = permissionService.findByCode(
                permissionService.findAll()
                        .stream()
                        .filter(p -> p.getId().equals(permissionId))
                        .findFirst()
                        .orElseThrow()
                        .getCode()
        );

        return ResponseEntity.ok(
                rolePermissionService.assignPermission(role, permission)
        );
    }
}
