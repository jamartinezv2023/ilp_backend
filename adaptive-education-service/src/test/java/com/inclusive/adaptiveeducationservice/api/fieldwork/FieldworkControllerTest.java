package com.inclusive.adaptiveeducationservice.api.fieldwork;

import com.inclusive.adaptiveeducationservice.fieldwork.dto.ConsentResponse;
import com.inclusive.adaptiveeducationservice.fieldwork.service.ConsentRecordNotFoundException;
import com.inclusive.adaptiveeducationservice.fieldwork.service.FieldworkService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FieldworkController.class)
class FieldworkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FieldworkService fieldworkService;

    @Test
    void withdrawsConsentThroughHttpPatch() throws Exception {

        UUID consentId =
                UUID.randomUUID();

        when(
                fieldworkService.withdrawConsent(
                        consentId
                )
        ).thenReturn(
                new ConsentResponse(
                        consentId,
                        "PARTICIPANT-HTTP-001",
                        "RESEARCH",
                        "WITHDRAWN"
                )
        );

        mockMvc.perform(
                        patch(
                                "/api/v1/fieldwork/consents/{consentId}/withdraw",
                                consentId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.consentId")
                                .value(
                                        consentId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.participantCode")
                                .value(
                                        "PARTICIPANT-HTTP-001"
                                )
                )
                .andExpect(
                        jsonPath("$.consentType")
                                .value(
                                        "RESEARCH"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(
                                        "WITHDRAWN"
                                )
                );
    }

    @Test
    void returns404WhenConsentDoesNotExist() throws Exception {

        UUID consentId =
                UUID.randomUUID();

        when(
                fieldworkService.withdrawConsent(
                        consentId
                )
        ).thenThrow(
                new ConsentRecordNotFoundException(
                        consentId
                )
        );

        mockMvc.perform(
                        patch(
                                "/api/v1/fieldwork/consents/{consentId}/withdraw",
                                consentId
                        )
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value(
                                        "Not Found"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Consent record not found: "
                                                + consentId
                                )
                );
    }
}