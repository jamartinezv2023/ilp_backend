package com.inclusive.adaptiveeducationservice.api.assessmentscientifichistory;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.GetParticipantAssessmentScientificHistoryService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.model.ParticipantAssessmentScientificHistory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        "/api/v1/participants"
)
public class ParticipantAssessmentScientificHistoryController {

    private final GetParticipantAssessmentScientificHistoryService
            service;

    public ParticipantAssessmentScientificHistoryController(
            GetParticipantAssessmentScientificHistoryService service
    ) {
        this.service = service;
    }

    @GetMapping(
            "/{participantId}/assessment-scientific-history"
    )
    public ResponseEntity<
            ParticipantAssessmentScientificHistoryResponse
            > getByParticipantId(
            @PathVariable String participantId
    ) {
        ParticipantAssessmentScientificHistory history =
                service.getByParticipantId(
                        participantId
                );

        return ResponseEntity.ok(
                new ParticipantAssessmentScientificHistoryResponse(
                        history.participantId(),
                        history.totalObservations(),
                        history.firstSubmittedAt(),
                        history.lastSubmittedAt(),
                        history.observations()
                )
        );
    }
}