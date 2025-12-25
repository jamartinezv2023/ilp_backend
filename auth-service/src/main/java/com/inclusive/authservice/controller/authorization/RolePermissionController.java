package com.inclusive.authservice.controller.authorization;

import com.inclusive.authservice.service.authorization.RolePermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth/role-permissions")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    public RolePermissionController(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    @PostMapping("/roles/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> assignPermission(
            @PathVariable UUID roleId,
            @PathVariable UUID permissionId
    ) {
        rolePermissionService.assignPermissionToRole(roleId, permissionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/roles/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermission(
            @PathVariable UUID roleId,
            @PathVariable UUID permissionId
    ) {
        rolePermissionService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.noContent().build();
    }
}