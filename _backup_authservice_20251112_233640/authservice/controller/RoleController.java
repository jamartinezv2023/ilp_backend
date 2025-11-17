package com.inclusive.authservice.controller;\r\nimport java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inclusive.authservice.model.Role;
import com.inclusive.authservice.service.RoleService;\r\n@RestController
@RequestMapping("/api/role")
public class RoleController {\r\n    @Autowired
    private RoleService service;\r\n    @GetMapping("/list")
    public ResponseEntity<List<Role>> list() { return ResponseEntity.ok(service.listAll()); }\r\n    @GetMapping("/{id}")
    public ResponseEntity<Role> get(@PathVariable Long id) { return ResponseEntity.ok(service.getById(id)); }\r\n    @PostMapping
    public ResponseEntity<Role> create(@RequestBody Role entity) { return ResponseEntity.ok(service.create(entity)); }\r\n    @PutMapping("/{id}")
    public ResponseEntity<Role> update(@PathVariable Long id, @RequestBody Role entity) { return ResponseEntity.ok(service.update(id, entity)); }\r\n    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}