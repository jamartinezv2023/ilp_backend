package com.inclusive.authservice.controller;\r\nimport java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inclusive.authservice.model.User;
import com.inclusive.authservice.service.UserService;\r\n@RestController
@RequestMapping("/api/user")
public class UserController {\r\n    @Autowired
    private UserService service;\r\n    @GetMapping("/list")
    public ResponseEntity<List<User>> list() { return ResponseEntity.ok(service.listAll()); }\r\n    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Long id) { return ResponseEntity.ok(service.getById(id)); }\r\n    @PostMapping
    public ResponseEntity<User> create(@RequestBody User entity) { return ResponseEntity.ok(service.create(entity)); }\r\n    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User entity) { return ResponseEntity.ok(service.update(id, entity)); }\r\n    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}