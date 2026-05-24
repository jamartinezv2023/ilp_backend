package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.PsychopedagogicalProfileResponse;
import com.inclusive.diagnosis.response.repository.DiagnosticResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PsychopedagogicalProfileService {

    private final DiagnosticResponseRepository responseRepository;

    public PsychopedagogicalProfileResponse generate(
            UUID studentProfileId
    ) {

        var responses = responseRepository.findByStudentProfileId(
                studentProfileId
        );

        long reflective = responses.stream()
                .filter(item -> "REFLECTIVE_OBSERVATION".equals(
                        item.getResponseValue()
                ))
                .count();

        long active = responses.stream()
                .filter(item -> "ACTIVE_EXPERIMENTATION".equals(
                        item.getResponseValue()
                ))
                .count();

        String kolbStyle = reflective >= active
                ? "REFLECTIVE_LEARNING_ORIENTATION"
                : "ACTIVE_LEARNING_ORIENTATION";

        String felderProfile =
                "PENDING_FELDER_SILVERMAN_RESPONSES";

        String kuderPreference =
                "PENDING_KUDER_VOCATIONAL_RESPONSES";

        String supportSummary =
                "Use inclusive evidence, DUA strategies and PIAR-oriented adjustments when support signals are detected.";

        String pathway =
                "Combine learning-style evidence, vocational preference signals and inclusive support needs before recommending an educational pathway.";

        List<String> mlSignals = List.of(
                "Structured questionnaire responses available",
                "Longitudinal intervention evidence expected",
                "Support dimensions can be used as supervised learning labels only after expert validation",
                "Clinical labels must not be inferred without professional evidence"
        );

        String warning =
                "This profile is educational and pedagogical. It must not be used as a clinical diagnosis or as the sole basis for vocational decisions.";

        return new PsychopedagogicalProfileResponse(
                studentProfileId.toString(),
                kolbStyle,
                felderProfile,
                kuderPreference,
                supportSummary,
                pathway,
                mlSignals,
                warning
        );
    }
}
