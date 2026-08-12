package com.inclusive.adaptiveeducationservice.api.fieldwork.researchidentity;

import com.inclusive.adaptiveeducationservice.fieldwork.application.researchidentity.ResearchIdentityAssignmentService;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectIdentity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(
        "/api/v1/fieldwork/research-participants"
)
public final class ResearchIdentityController {

    private final ResearchIdentityAssignmentService
            assignmentService;

    public ResearchIdentityController(
            ResearchIdentityAssignmentService assignmentService
    ) {
        this.assignmentService =
                Objects.requireNonNull(
                        assignmentService,
                        "assignmentService is required"
                );
    }

    @PostMapping(
            "/{participantUuid}/research-identity"
    )
    public ResponseEntity<ResearchIdentityResponse>
    assignResearchIdentity(
            @PathVariable UUID participantUuid
    ) {
        ResearchSubjectIdentity identity =
                assignmentService.assign(
                        participantUuid
                );

        ResearchIdentityResponse response =
                new ResearchIdentityResponse(
                        identity.getParticipantUuid(),
                        identity
                                .getResearchSubjectId()
                                .value(),
                        identity.getCreatedAt(),
                        identity.isActive()
                );

        return ResponseEntity.ok(
                response
        );
    }
}
