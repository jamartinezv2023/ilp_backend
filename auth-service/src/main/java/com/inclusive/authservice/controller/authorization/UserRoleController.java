package com.inclusive.authservice.controller.authorization;

import com.inclusive.authservice.entity.authorization.UserRole;
import com.inclusive.authservice.service.authorization.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user-roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping
    public ResponseEntity<UserRole> assign(
            @RequestParam UUID userId,
            @RequestParam UUID roleId
    ) {
        return ResponseEntity.ok(
                userRoleService.assignRole(userId, roleId)
        );
    }

    @DeleteMapping("/{userRoleId}")
    public ResponseEntity<Void> revoke(@PathVariable UUID userRoleId) {
        userRoleService.revokeRole(userRoleId);
        return ResponseEntity.noContent().build();
    }
}
