package com.inclusive.adaptiveeducationservice.api;

import com.inclusive.adaptiveeducationservice.domain.adaptation.AdaptationRule;
import com.inclusive.adaptiveeducationservice.domain.adaptation.repository.AdaptationRuleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/adaptation-rules")
public class AdaptationRuleController {

    private final AdaptationRuleRepository repository;

    public AdaptationRuleController(AdaptationRuleRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<AdaptationRule> getAllRules() {
        return repository.findAll();
    }

    @PostMapping
    public AdaptationRule createRule(@RequestBody AdaptationRule rule) {
        return repository.save(rule);
    }
}
