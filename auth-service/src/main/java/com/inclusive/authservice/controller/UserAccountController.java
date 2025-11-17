package com.inclusive.authservice.controller;

import com.inclusive.authservice.dto.UserAccountDTO;
import com.inclusive.authservice.service.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * REST controller exposing CRUD endpoints for UserAccount.
 *
 * Base path:
 *   /api/auth/users
 */
@RestController
@RequestMapping("/api/auth/users")
@Validated
public class UserAccountController {

    private final UserAccountService service;

    public UserAccountController(UserAccountService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UserAccountDTO>> getAll() {
        List<UserAccountDTO> users = service.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccountDTO> getById(@PathVariable Long id) {
        UserAccountDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UserAccountDTO> create(@Valid @RequestBody UserAccountDTO dto) {
        UserAccountDTO created = service.create(dto);
        URI location = URI.create("/api/auth/users/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAccountDTO> update(@PathVariable Long id,
                                                 @Valid @RequestBody UserAccountDTO dto) {
        UserAccountDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
