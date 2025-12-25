// Location: auth-service/src/main/java/com/inclusive/authservice/controller/UserAccountController.java
package com.inclusive.authservice.controller;

import com.inclusive.authservice.dto.CreateUserAccountRequest;
import com.inclusive.authservice.dto.UpdateUserAccountRequest;
import com.inclusive.authservice.dto.UserAccountDTO;
import com.inclusive.authservice.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-accounts")
public class UserAccountController {

    private final UserAccountService service;

    public UserAccountController(UserAccountService service) {
        this.service = service;
    }

    // =========================
    // READ
    // =========================

    @GetMapping
    public List<UserAccountDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public UserAccountDTO findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    // =========================
    // CREATE
    // =========================

    @PostMapping
    public ResponseEntity<UserAccountDTO> create(
            @Valid @RequestBody CreateUserAccountRequest request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    // =========================
    // UPDATE
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<UserAccountDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserAccountRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    // =========================
    // DELETE
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
