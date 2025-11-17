package com.inclusive.authservice.controller;\r\nimport java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inclusive.authservice.model.UserMfa;
import com.inclusive.authservice.service.UserMfaService;\r\n@RestController
@RequestMapping("/api/usermfa")
public class UserMfaController {\r\n    @Autowired
    private UserMfaService service;\r\n    @GetMapping("/list")
    public ResponseEntity<List<UserMfa>> list() { return ResponseEntity.ok(service.listAll()); }\r\n    @GetMapping("/{id}")
    public ResponseEntity<UserMfa> get(@PathVariable Long id) { return ResponseEntity.ok(service.getById(id)); }\r\n    @PostMapping
    public ResponseEntity<UserMfa> create(@RequestBody UserMfa entity) { return ResponseEntity.ok(service.create(entity)); }\r\n    @PutMapping("/{id}")
    public ResponseEntity<UserMfa> update(@PathVariable Long id, @RequestBody UserMfa entity) { return ResponseEntity.ok(service.update(id, entity)); }\r\n    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}