package com.inclusive.adaptiveeducationservice.api.scientificfeaturestore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.ScientificFeatureVectorQueryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ScientificFeatureApiExceptionHandlerMockMvcTest {

    private static final String BASE_PATH =
            "/api/v1/scientific-feature-store";

    private ScientificFeatureVectorQueryUseCase queryUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        queryUseCase =
                mock(
                        ScientificFeatureVectorQueryUseCase.class
                );

        ObjectMapper objectMapper =
                new ObjectMapper()
                        .registerModule(
                                new JavaTimeModule()
                        )
                        .disable(
                                SerializationFeature
                                        .WRITE_DATES_AS_TIMESTAMPS
                        );

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(
                                new ScientificFeatureVectorController(
                                        queryUseCase
                                )
                        )
                        .setControllerAdvice(
                                new ScientificFeatureApiExceptionHandler()
                        )
                        .setMessageConverters(
                                new MappingJackson2HttpMessageConverter(
                                        objectMapper
                                )
                        )
                        .build();
    }

    @Test
    void shouldReturnUniformErrorForMissingParameter()
            throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/exact")
                                .param(
                                        "featureSetVersion",
                                        "FEATURES-V1"
                                )
                                .param(
                                        "featureCutoffAt",
                                        "2026-08-02T12:00:00Z"
                                )
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.error")
                                .value("Bad Request")
                )
                .andExpect(
                        jsonPath("$.code")
                                .value(
                                        "SCIENTIFIC_FEATURE_MISSING_PARAMETER"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Required parameter is missing: participantId"
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(BASE_PATH + "/exact")
                );

        verifyNoInteractions(queryUseCase);
    }

    @Test
    void shouldReturnUniformErrorForInvalidInstant()
            throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/exact")
                                .param(
                                        "participantId",
                                        "PARTICIPANT-001"
                                )
                                .param(
                                        "featureSetVersion",
                                        "FEATURES-V1"
                                )
                                .param(
                                        "featureCutoffAt",
                                        "invalid-instant"
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.code")
                                .value(
                                        "SCIENTIFIC_FEATURE_INVALID_PARAMETER_FORMAT"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "featureCutoffAt has an invalid format"
                                )
                );

        verifyNoInteractions(queryUseCase);
    }

    @Test
    void shouldReturnUniformErrorForBlankParticipant()
            throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/exact")
                                .param("participantId", " ")
                                .param(
                                        "featureSetVersion",
                                        "FEATURES-V1"
                                )
                                .param(
                                        "featureCutoffAt",
                                        "2026-08-02T12:00:00Z"
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.code")
                                .value(
                                        "SCIENTIFIC_FEATURE_INVALID_REQUEST"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "participantId must not be blank"
                                )
                );
    }

    @Test
    void shouldReturnUniformErrorForBlankFeatureSetVersion()
            throws Exception {
        mockMvc.perform(
                        get(
                                BASE_PATH
                                        + "/latest-completed/"
                                        + "PARTICIPANT-001"
                        )
                                .param(
                                        "featureSetVersion",
                                        " "
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.code")
                                .value(
                                        "SCIENTIFIC_FEATURE_INVALID_REQUEST"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "featureSetVersion must not be blank"
                                )
                );
    }

    @Test
    void shouldReturnUniformErrorForExactNotFound()
            throws Exception {
        when(
                queryUseCase.findExact(any())
        ).thenReturn(
                Optional.empty()
        );

        mockMvc.perform(
                        get(BASE_PATH + "/exact")
                                .param(
                                        "participantId",
                                        "PARTICIPANT-001"
                                )
                                .param(
                                        "featureSetVersion",
                                        "FEATURES-V1"
                                )
                                .param(
                                        "featureCutoffAt",
                                        "2026-08-02T12:00:00Z"
                                )
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.error")
                                .value("Not Found")
                )
                .andExpect(
                        jsonPath("$.code")
                                .value(
                                        "SCIENTIFIC_FEATURE_NOT_FOUND"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Exact scientific feature vector not found"
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(BASE_PATH + "/exact")
                );
    }

    @Test
    void shouldReturnUniformErrorForUnexpectedFailure()
            throws Exception {
        when(
                queryUseCase.findExact(any())
        ).thenThrow(
                new IllegalStateException(
                        "sensitive internal failure"
                )
        );

        mockMvc.perform(
                        get(BASE_PATH + "/exact")
                                .param(
                                        "participantId",
                                        "PARTICIPANT-001"
                                )
                                .param(
                                        "featureSetVersion",
                                        "FEATURES-V1"
                                )
                                .param(
                                        "featureCutoffAt",
                                        "2026-08-02T12:00:00Z"
                                )
                )
                .andExpect(
                        status().isInternalServerError()
                )
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(
                        jsonPath("$.error")
                                .value(
                                        "Internal Server Error"
                                )
                )
                .andExpect(
                        jsonPath("$.code")
                                .value(
                                        "SCIENTIFIC_FEATURE_INTERNAL_ERROR"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "An unexpected scientific feature error occurred"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "An unexpected scientific feature error occurred"
                                )
                );
    }
}
