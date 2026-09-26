package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Set;

@Component
public class ControlledAssessmentModePolicy {

    private static final Set<String> CANDIDATE_CODES = Set.of(
            "ILP-EPL", "ILP-MEA", "ILP-IVP"
    );

    public boolean validateAndIsDemo(SubmitAssessmentRequest request) {
        String mode = request.context()
                .getOrDefault("executionMode", "")
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!CANDIDATE_CODES.contains(request.assessmentCode())) {
            // No instrument has an approved fieldwork record yet. An active
            // renderer definition alone cannot authorize participant data.
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No instrument is approved for fieldwork collection"
            );
        }

        if (!"DEMO".equals(mode)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Candidate instruments are enabled only for controlled DEMO use"
            );
        }

        if (!request.participantId().startsWith("DEMO-")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "DEMO submissions require a synthetic participant identifier"
            );
        }

        requireContext(request, "language");
        requireContext(request, "translationVersion");
        return true;
    }

    private void requireContext(
            SubmitAssessmentRequest request,
            String key
    ) {
        if (request.context().getOrDefault(key, "").isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Missing traceability context: " + key
            );
        }
    }
}
