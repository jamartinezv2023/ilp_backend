package com.inclusive.adaptiveeducationservice.api.scientific;

import com.inclusive.adaptiveeducationservice.research.production.ScientificActor;
import com.inclusive.adaptiveeducationservice.research.production.ScientificApiFilter;
import com.inclusive.adaptiveeducationservice.research.production.ScientificProductionService;
import com.inclusive.adaptiveeducationservice.research.production.ScientificSnapshot;

import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

@RestController
@Profile("scientific-production")
public class ScientificProductionController {
    private final ScientificProductionService service;

    public ScientificProductionController(ScientificProductionService service) { this.service = service; }

    private static ScientificActor actor(HttpServletRequest request) {
        var value = request.getAttribute(ScientificApiFilter.ACTOR_ATTRIBUTE);
        if (!(value instanceof ScientificActor verified)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return verified;
    }

    @PostMapping("/api/v1/assessment-submissions")
    public ResponseEntity<SubmitAssessmentResponse> submit(HttpServletRequest http,
            @RequestHeader("X-Tenant-Id") UUID tenant,
            @RequestHeader("X-Scientific-Grant") UUID assignment,
            @Valid @RequestBody SubmitAssessmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.submit(actor(http), tenant, assignment, request));
    }

    @GetMapping("/api/v1/scientific-applications/{assignment}/history")
    public List<AssessmentScientificObservation> history(HttpServletRequest http,
            @RequestHeader("X-Tenant-Id") UUID tenant, @PathVariable UUID assignment) {
        return service.history(actor(http), tenant, assignment);
    }

    @GetMapping("/api/v1/scientific-applications/{assignment}/administrations/{administrationId}/snapshot")
    public ScientificSnapshot snapshot(HttpServletRequest http, @RequestHeader("X-Tenant-Id") UUID tenant,
            @PathVariable UUID assignment, @PathVariable String administrationId) {
        return service.snapshot(actor(http), tenant, assignment, administrationId);
    }

    @PostMapping("/api/v1/scientific-applications/consents/{evidenceId}/withdraw")
    public ResponseEntity<Void> withdraw(HttpServletRequest http,
            @RequestHeader("X-Tenant-Id") UUID tenant, @PathVariable UUID evidenceId) {
        service.withdraw(actor(http), tenant, evidenceId);
        return ResponseEntity.noContent().build();
    }
}
