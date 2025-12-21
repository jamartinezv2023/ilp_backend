package com.inclusive.authservice.controller.authorization;

import com.inclusive.authservice.dto.authorization.CreateRoleRequest;
import com.inclusive.authservice.dto.authorization.domain.RoleDTO;
import com.inclusive.authservice.dto.authorization.domain.RolePermissionDTO;
import com.inclusive.authservice.entity.authorization.Role;
import com.inclusive.authservice.mapper.authorization.RoleMapper;
import com.inclusive.authservice.service.authorization.RolePermissionService;
import com.inclusive.authservice.service.authorization.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/roles")
public class RoleController {

    private final RoleService roleService;
    private final RolePermissionService rolePermissionService;
    private final RoleMapper roleMapper;

    public RoleController(
            RoleService roleService,
            RolePermissionService rolePermissionService,
            RoleMapper roleMapper
    ) {
        this.roleService = roleService;
        this.rolePermissionService = rolePermissionService;
        this.roleMapper = roleMapper;
    }

    @PostMapping
    public ResponseEntity<RoleDTO> create(@RequestBody CreateRoleRequest request) {
        Role role = roleService.create(
                request.getName(),
                request.getDescription()
        );

        return ResponseEntity.ok(roleMapper.toDto(role));
    }

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getActiveRoles() {
        List<RoleDTO> roles = roleService.getActiveRoles()
                .stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<List<RolePermissionDTO>> getPermissionsByRole(
            @PathVariable UUID roleId
    ) {
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