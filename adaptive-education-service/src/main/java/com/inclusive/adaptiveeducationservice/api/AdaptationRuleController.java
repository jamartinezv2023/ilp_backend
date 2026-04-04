package com.inclusive.adaptiveeducationservice.api;

import com.inclusive.adaptiveeducationservice.domain.adaptation.AdaptationRule;
import com.inclusive.adaptiveeducationservice.domain.adaptation.repository.AdaptationRuleRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/adaptation-rules")
public class AdaptationRuleController {

    private final AdaptationRuleRepository repo;

    public AdaptationRuleController(AdaptationRuleRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<AdaptationRule> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdaptationRule> byId(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AdaptationRule> create(@Valid @RequestBody AdaptationRuleRequest req) {
        AdaptationRule rule = new AdaptationRule(req.tenantId, req.name, req.conditionExpression, req.actionExpression);
        rule.setEnabled(req.enabled);
        AdaptationRule saved = repo.save(rule);
        return ResponseEntity.created(URI.create("/api/adaptation-rules/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdaptationRule> update(@PathVariable Long id, @Valid @RequestBody AdaptationRuleRequest req) {
        return repo.findById(id).map(existing -> {
            existing.setTenantId(req.tenantId);
            existing.setName(req.name);
            existing.setConditionExpression(req.conditionExpression);
            existing.setActionExpression(req.actionExpression);
            existing.setEnabled(req.enabled);
            AdaptationRule saved = repo.save(existing);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
