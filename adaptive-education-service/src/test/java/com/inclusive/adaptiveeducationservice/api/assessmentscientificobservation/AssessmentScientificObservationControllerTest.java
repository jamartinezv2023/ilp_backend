package com.inclusive.adaptiveeducationservice.api.assessmentscientificobservation;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.AssessmentScientificObservationNotFoundException;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.GetAssessmentScientificObservationService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.ScientificSubmissionContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        AssessmentScientificObservationController.class
)
class AssessmentScientificObservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetAssessmentScientificObservationService service;

    @Test
    void shouldReturnScientificObservation() throws Exception {
        when(
                service.getByAdministrationId(
                        "ADMIN-001"
                )
        ).thenReturn(
                observation()
        );

        mockMvc.perform(
                get(
                        "/api/v1/assessment-scientific-observations/{administrationId}",
                        "ADMIN-001"
                )
        )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.administrationId")
                                .value("ADMIN-001")
                )
                .andExpect(
                        jsonPath("$.participantId")
                                .value("ST-001")
                )
                .andExpect(
                        jsonPath("$.assessmentCode")
                                .value("KOLB_V1")
                )
                .andExpect(
                        jsonPath("$.scores")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$.interpretations")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$.context.institutionId")
                                .value("INST-001")
                );
    }

    @Test
    void shouldReturnNotFoundForMissingObservation()
            throws Exception {
        when(
                service.getByAdministrationId(
                        "ADMIN-404"
                )
        ).thenThrow(
                new AssessmentScientificObservationNotFoundException(
                        "ADMIN-404"
                )
        );

        mockMvc.perform(
                get(
                        "/api/v1/assessment-scientific-observations/{administrationId}",
                        "ADMIN-404"
                )
        )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.title")
                                .value(
                                        "Scientific assessment observation not found"
                                )
                );
    }

    private AssessmentScientificObservation observation() {
        Instant timestamp =
                Instant.parse(
                        "2026-07-24T10:00:00Z"
                );

        ScientificSubmissionContext context =
                new ScientificSubmissionContext(
                        "INST-001",
                        "CAMPUS-001",
                        "PROGRAM-001",
                        "COURSE-001",
                        "COHORT-001",
                        "TEACHER-001",
                        "10",
                        "2026",
                        "1",
                        "PILOT",
                        null,
                        "CONTROL",
                        "CONSENT-001",
                        "1.0",
                        "ETHICS-001",
                        "WEB",
                        "ONLINE",
                        "es",
                        "DESKTOP",
                        "Chrome",
                        "Windows",
                        "America/Bogota",
                        "MVP-21A",
                        "FEATURES-V1",
                        "PREPROCESSING-V1",
                        "NORMALIZATION-V1",
                        timestamp.minusSeconds(600),
                        600L,
                        timestamp,
                        Map.of(
                                "source",
                                "WEB"
                        )
                );

        return new AssessmentScientificObservation(
                "ADMIN-001",
                "ST-001",
                "KOLB_V1",
                "1.0",
                "DIVERGENT",
                "KOLB_BASELINE_V1",
                "KOLB_INTERPRETATION_V1",
                timestamp,
                timestamp,
                timestamp,
                List.of(),
                List.of(),
                context
        );
    }
}