package com.inclusive.authservice.controller;\r\nimport java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inclusive.authservice.model.VerificationToken;
import com.inclusive.authservice.service.VerificationTokenService;\r\n@RestController
@RequestMapping("/api/verificationtoken")
public class VerificationTokenController {\r\n    @Autowired
    private VerificationTokenService service;\r\n    @GetMapping("/list")
    public ResponseEntity<List<VerificationToken>> list() { return ResponseEntity.ok(service.listAll()); }\r\n    @GetMapping("/{id}")
    public ResponseEntity<VerificationToken> get(@PathVariable Long id) { return ResponseEntity.ok(service.getById(id)); }\r\n    @PostMapping
    public ResponseEntity<VerificationToken> create(@RequestBody VerificationToken entity) { return ResponseEntity.ok(service.create(entity)); }\r\n    @PutMapping("/{id}")
    public ResponseEntity<VerificationToken> update(@PathVariable Long id, @RequestBody VerificationToken entity) { return ResponseEntity.ok(service.update(id, entity)); }\r\n    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}