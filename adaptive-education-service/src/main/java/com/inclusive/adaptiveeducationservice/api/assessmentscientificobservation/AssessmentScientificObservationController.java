package com.inclusive.adaptiveeducationservice.api.assessmentscientificobservation;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.GetAssessmentScientificObservationService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        "/api/v1/assessment-scientific-observations"
)
public class AssessmentScientificObservationController {

    private final GetAssessmentScientificObservationService
            service;

    public AssessmentScientificObservationController(
            GetAssessmentScientificObservationService service
    ) {
        this.service = service;
    }

    @GetMapping("/{administrationId}")
    public ResponseEntity<AssessmentScientificObservationResponse>
    getByAdministrationId(
            @PathVariable String administrationId
    ) {
        AssessmentScientificObservation observation =
                service.getByAdministrationId(
                        administrationId
                );

        return ResponseEntity.ok(
                toResponse(observation)
        );
    }

    private AssessmentScientificObservationResponse toResponse(
            AssessmentScientificObservation observation
    ) {
        return new AssessmentScientificObservationResponse(
                observation.administrationId(),
                observation.participantId(),
                observation.assessmentCode(),
                observation.assessmentVersion(),
                observation.primaryProfile(),
                observation.scoringAlgorithmVersion(),
                observation.interpretationVersion(),
                observation.calculatedAt(),
                observation.submittedAt(),
                observation.featureCutoffAt(),
                observation.scores(),
                observation.interpretations(),
                observation.context()
        );
    }
}