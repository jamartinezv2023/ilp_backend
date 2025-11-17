package com.inclusive.authservice.controller;\r\nimport java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inclusive.authservice.model.StudentSupportNeeds;
import com.inclusive.authservice.service.StudentSupportNeedsService;\r\n@RestController
@RequestMapping("/api/studentsupportneeds")
public class StudentSupportNeedsController {\r\n    @Autowired
    private StudentSupportNeedsService service;\r\n    @GetMapping("/list")
    public ResponseEntity<List<StudentSupportNeeds>> list() { return ResponseEntity.ok(service.listAll()); }\r\n    @GetMapping("/{id}")
    public ResponseEntity<StudentSupportNeeds> get(@PathVariable Long id) { return ResponseEntity.ok(service.getById(id)); }\r\n    @PostMapping
    public ResponseEntity<StudentSupportNeeds> create(@RequestBody StudentSupportNeeds entity) { return ResponseEntity.ok(service.create(entity)); }\r\n    @PutMapping("/{id}")
    public ResponseEntity<StudentSupportNeeds> update(@PathVariable Long id, @RequestBody StudentSupportNeeds entity) { return ResponseEntity.ok(service.update(id, entity)); }\r\n    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}