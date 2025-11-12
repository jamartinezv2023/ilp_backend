package com.inclusive.authservice.controller;\r\nimport java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inclusive.authservice.model.StudentEnvironment;
import com.inclusive.authservice.service.StudentEnvironmentService;\r\n@RestController
@RequestMapping("/api/studentenvironment")
public class StudentEnvironmentController {\r\n    @Autowired
    private StudentEnvironmentService service;\r\n    @GetMapping("/list")
    public ResponseEntity<List<StudentEnvironment>> list() { return ResponseEntity.ok(service.listAll()); }\r\n    @GetMapping("/{id}")
    public ResponseEntity<StudentEnvironment> get(@PathVariable Long id) { return ResponseEntity.ok(service.getById(id)); }\r\n    @PostMapping
    public ResponseEntity<StudentEnvironment> create(@RequestBody StudentEnvironment entity) { return ResponseEntity.ok(service.create(entity)); }\r\n    @PutMapping("/{id}")
    public ResponseEntity<StudentEnvironment> update(@PathVariable Long id, @RequestBody StudentEnvironment entity) { return ResponseEntity.ok(service.update(id, entity)); }\r\n    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}