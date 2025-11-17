package com.inclusive.tenantservice.controller;

import com.inclusive.tenantservice.dto.TenantDTO;
import com.inclusive.tenantservice.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping
    public ResponseEntity<List<TenantDTO>> getAllTenants() {
        return ResponseEntity.ok(tenantService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantDTO> getTenantById(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TenantDTO> createTenant(@RequestBody TenantDTO dto) {
        TenantDTO created = tenantService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantDTO> updateTenant(@PathVariable Long id, @RequestBody TenantDTO dto) {
        TenantDTO updated = tenantService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        tenantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}