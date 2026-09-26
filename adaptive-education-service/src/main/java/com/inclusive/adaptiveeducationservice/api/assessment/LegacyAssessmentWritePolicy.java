package com.inclusive.adaptiveeducationservice.api.assessment;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/** Keeps historical reads available while blocking unapproved legacy writes. */
@Component
public class LegacyAssessmentWritePolicy {

    public void rejectNewSubmission(String legacyInstrument) {
        throw new ResponseStatusException(
                HttpStatus.GONE,
                legacyInstrument
                        + " submission is disabled pending institutional and instrument approval"
        );
    }
}
