package com.inclusive.adaptiveeducationservice.api.assessmentsubmission;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inclusive.adaptiveeducationservice.api.assessment.SyntheticLoopbackKolbController;
import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.InvalidAssessmentSubmissionException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
class AssessmentSubmissionExceptionHandlerHttpTest {
    @Test
    void rejectionReturnsStableProblemWithoutExposingInternalDetails() throws Exception {
        var service = mock(KolbAssessmentService.class);
        var request = new KolbAssessmentRequest(
                "SYNTHETIC-STUDENT-001", Collections.nCopies(48, 1)
        );
        String internalDetail = "INTERNAL-SYNTHETIC-DIAGNOSTIC";
        doThrow(new InvalidAssessmentSubmissionException(internalDetail))
                .when(service).submit(request);
        var mvc = MockMvcBuilders.standaloneSetup(
                new SyntheticLoopbackKolbController(service)
        ).setControllerAdvice(
                new AssessmentSubmissionExceptionHandler()
        ).build();
        mvc.perform(post("/api/v1/assessments/kolb")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.title").value(
                        "Invalid assessment submission"
                ))
                .andExpect(jsonPath("$.code").value(
                        "ASSESSMENT_SUBMISSION_INVALID"
                ))
                .andExpect(jsonPath("$.detail").value(
                        "The assessment responses do not satisfy the instrument rules."
                ))
                .andExpect(content().string(not(containsString(internalDetail))));
        verify(service).submit(request);
    }
}
