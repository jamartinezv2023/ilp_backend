package com.inclusive.adaptiveeducationservice.controller;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;
import com.inclusive.adaptiveeducationservice.service.StudentIntegrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proxy/students")
public class StudentProxyController {

    private final StudentIntegrationService integrationService;

    public StudentProxyController(StudentIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @GetMapping
    public List<StudentDTO> getAll() {
        return integrationService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(@PathVariable Long id) {
        return integrationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StudentDTO> create(@RequestBody StudentDTO dto) {
        StudentDTO created = integrationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> update(@PathVariable Long id, @RequestBody StudentDTO dto) {
        return integrationService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        integrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
