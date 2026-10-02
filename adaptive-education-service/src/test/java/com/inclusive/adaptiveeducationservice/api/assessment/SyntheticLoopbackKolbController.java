package com.inclusive.adaptiveeducationservice.api.assessment;
import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/** Test-only adapter registered manually in standalone MockMvc. */
@RestController
@Profile("kolb-loopback-only")
@RequestMapping("/api/v1/assessments/kolb")
public class SyntheticLoopbackKolbController extends KolbAssessmentController {
    private final KolbAssessmentService service;
    public SyntheticLoopbackKolbController(KolbAssessmentService service) {
        super(service, new LegacyAssessmentWritePolicy());
        this.service = service;
    }
    @Override
    @PostMapping
    public ResponseEntity<KolbAssessmentResponse> submit(
            @Valid @RequestBody KolbAssessmentRequest request
    ) {
        return ResponseEntity.ok(service.submit(request));
    }
}
