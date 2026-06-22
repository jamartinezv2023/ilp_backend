package com.inclusive.adaptiveeducationservice.api.fieldwork;

import com.inclusive.adaptiveeducationservice.fieldwork.dto.ConsentRequest;
import com.inclusive.adaptiveeducationservice.fieldwork.dto.ConsentResponse;
import com.inclusive.adaptiveeducationservice.fieldwork.dto.FieldworkReadinessResponse;
import com.inclusive.adaptiveeducationservice.fieldwork.dto.ParticipantRequest;
import com.inclusive.adaptiveeducationservice.fieldwork.dto.ParticipantResponse;
import com.inclusive.adaptiveeducationservice.fieldwork.service.FieldworkService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fieldwork")
public class FieldworkController {

    private final FieldworkService service;

    public FieldworkController(FieldworkService service) {
        this.service = service;
    }

    @PostMapping("/consents")
    @ResponseStatus(HttpStatus.CREATED)
    public ConsentResponse registerConsent(@RequestBody ConsentRequest request) {
        return service.registerConsent(request);
    }

    @PostMapping("/participants")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipantResponse createParticipant(@RequestBody ParticipantRequest request) {
        return service.createParticipant(request);
    }

    @GetMapping("/readiness")
    public FieldworkReadinessResponse readiness() {
        return new FieldworkReadinessResponse(true, "Fieldwork MVP vertical 1 is available for demo data.");
    }
}
