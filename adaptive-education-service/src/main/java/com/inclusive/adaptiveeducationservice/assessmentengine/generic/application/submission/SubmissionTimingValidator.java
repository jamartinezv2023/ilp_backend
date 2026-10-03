package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

/** Checks supplied client timing; it does not certify time spent on task. */
public final class SubmissionTimingValidator {
    private SubmissionTimingValidator() {
    }

    public static void validate(SubmitAssessmentRequest request) {
        String start = request.context().get("startedAt");
        String seconds = request.context().get("durationSeconds");
        if (start == null && seconds == null) {
            return;
        }
        if (start == null || seconds == null) {
            throw invalid();
        }
        try {
            Instant startedAt = Instant.parse(start);
            long duration = Long.parseLong(seconds);
            if (duration < 0 || startedAt.isAfter(request.submittedAt())
                    || duration != Duration.between(startedAt, request.submittedAt()).getSeconds()) {
                throw invalid();
            }
        } catch (DateTimeParseException | NumberFormatException exception) {
            throw invalid();
        }
    }

    private static ResponseStatusException invalid() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "ASSESSMENT_TIMING_INVALID");
    }
}
