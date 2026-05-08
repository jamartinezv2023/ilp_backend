package com.inclusive.authservice.controller.authorization;

import com.inclusive.authservice.entity.authorization.Role;
import com.inclusive.authservice.service.authorization.RolePermissionService;
import com.inclusive.authservice.service.authorization.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RolePermissionService rolePermissionService;

    @PostMapping
    public ResponseEntity<Role> create(@RequestBody Role request) {
        return ResponseEntity.ok(roleService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<Role>> findAll() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Role> findById(@PathVariable UUID roleId) {
        return ResponseEntity.ok(roleService.findById(roleId));
    }

    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<?> getPermissions(@PathVariable UUID roleId) {
        return ResponseEntity.ok(
                rolePermissionService.getPermissionsByRole(roleId)
        );
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID roleId) {
        roleService.deactivate(roleId);
        return ResponseEntity.noContent().build();
    }
}
