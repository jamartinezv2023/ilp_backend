package com.inclusive.authservice.controller;\r\nimport java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inclusive.authservice.model.StudentLearningProfile;
import com.inclusive.authservice.service.StudentLearningProfileService;\r\n@RestController
@RequestMapping("/api/studentlearningprofile")
public class StudentLearningProfileController {\r\n    @Autowired
    private StudentLearningProfileService service;\r\n    @GetMapping("/list")
    public ResponseEntity<List<StudentLearningProfile>> list() { return ResponseEntity.ok(service.listAll()); }\r\n    @GetMapping("/{id}")
    public ResponseEntity<StudentLearningProfile> get(@PathVariable Long id) { return ResponseEntity.ok(service.getById(id)); }\r\n    @PostMapping
    public ResponseEntity<StudentLearningProfile> create(@RequestBody StudentLearningProfile entity) { return ResponseEntity.ok(service.create(entity)); }\r\n    @PutMapping("/{id}")
    public ResponseEntity<StudentLearningProfile> update(@PathVariable Long id, @RequestBody StudentLearningProfile entity) { return ResponseEntity.ok(service.update(id, entity)); }\r\n    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}