package com.inclusive.adaptiveeducationservice.api.fieldwork.researchidentity;

import com.inclusive.adaptiveeducationservice.fieldwork.application.researchidentity.ResearchConsentRequiredException;
import com.inclusive.adaptiveeducationservice.fieldwork.application.researchidentity.ResearchIdentityAssignmentService;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectId;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectIdentity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResearchIdentityController.class)
class ResearchIdentityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResearchIdentityAssignmentService
            assignmentService;

    @Test
    void assignsResearchIdentityWithoutRecruitmentCode() throws Exception {
        UUID participantUuid =
                UUID.randomUUID();

        UUID researchSubjectUuid =
                UUID.randomUUID();

        ResearchSubjectIdentity identity =
                new ResearchSubjectIdentity(
                        participantUuid,
                        new ResearchSubjectId(
                                researchSubjectUuid
                        )
                );

        when(
                assignmentService.assign(
                        participantUuid
                )
        ).thenReturn(identity);

        mockMvc.perform(
                        post(
                                "/api/v1/fieldwork/research-participants/{participantUuid}/research-identity",
                                participantUuid
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.participantUuid")
                                .value(
                                        participantUuid.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.researchSubjectId")
                                .value(
                                        researchSubjectUuid.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.active")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.participantCode")
                                .doesNotExist()
                );
    }

    @Test
    void rejectsResearchIdentityAssignmentWithoutActiveConsent()
            throws Exception {

        UUID participantUuid =
                UUID.randomUUID();

        when(
                assignmentService.assign(
                        participantUuid
                )
        ).thenThrow(
                new ResearchConsentRequiredException(
                        participantUuid
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/fieldwork/research-participants/{participantUuid}/research-identity",
                                participantUuid
                        )
                )
                .andExpect(
                        status().isForbidden()
                );
    }
}
