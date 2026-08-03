package com.inclusive.adaptiveeducationservice.api.scientificfeaturestore;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.ScientificFeatureVectorQueryUseCase;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindExactScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindLatestScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result.ScientificFeatureItemResult;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result.ScientificFeatureVectorResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ScientificFeatureVectorControllerMockMvcTest {

    private static final String BASE_PATH =
            "/api/v1/scientific-feature-store";

    private static final String PARTICIPANT_ID =
            "PARTICIPANT-MOCKMVC-001";

    private static final String FEATURE_SET_VERSION =
            "SCIENTIFIC-FEATURES-V1";

    private static final Instant FEATURE_CUTOFF_AT =
            Instant.parse(
                    "2026-08-02T12:00:00Z"
            );

    private ScientificFeatureVectorQueryUseCase queryUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        queryUseCase =
                mock(
                        ScientificFeatureVectorQueryUseCase.class
                );

        ScientificFeatureVectorController controller =
                new ScientificFeatureVectorController(
                        queryUseCase
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
                        .standaloneSetup(controller)
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
    void shouldResolveExactRouteBindParametersAndSerializeJson()
            throws Exception {
        when(
                queryUseCase.findExact(any())
        ).thenReturn(
                Optional.of(
                        result(
                                "VECTOR-EXACT-MOCKMVC-001"
                        )
                )
        );

        mockMvc.perform(
                        get(BASE_PATH + "/exact")
                                .param(
                                        "participantId",
                                        PARTICIPANT_ID
                                )
                                .param(
                                        "featureSetVersion",
                                        FEATURE_SET_VERSION
                                )
                                .param(
                                        "featureCutoffAt",
                                        FEATURE_CUTOFF_AT.toString()
                                )
                                .accept(
                                        MediaType.APPLICATION_JSON
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(
                        jsonPath("$.vectorId")
                                .value(
                                        "VECTOR-EXACT-MOCKMVC-001"
                                )
                )
                .andExpect(
                        jsonPath("$.participantId")
                                .value(PARTICIPANT_ID)
                )
                .andExpect(
                        jsonPath("$.featureSetVersion")
                                .value(FEATURE_SET_VERSION)
                )
                .andExpect(
                        jsonPath("$.generatorVersion")
                                .value("GENERATOR-V1")
                )
                .andExpect(
                        jsonPath("$.featureCutoffAt")
                                .value(
                                        FEATURE_CUTOFF_AT.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.generatedAt")
                                .value(
                                        FEATURE_CUTOFF_AT
                                                .plusSeconds(5)
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.sourceObservationCount")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.checksum")
                                .value("CHECKSUM-MOCKMVC-001")
                )
                .andExpect(
                        jsonPath("$.features.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.features[0].itemId")
                                .value("ITEM-MOCKMVC-001")
                )
                .andExpect(
                        jsonPath("$.features[0].featureCode")
                                .value("KOLB_CE")
                )
                .andExpect(
                        jsonPath("$.features[0].dataType")
                                .value("NUMERIC")
                )
                .andExpect(
                        jsonPath("$.features[0].numericValue")
                                .value(25.0)
                )
                .andExpect(
                        jsonPath("$.features[0].textValue")
                                .doesNotExist()
                )
                .andExpect(
                        jsonPath("$.features[0].booleanValue")
                                .doesNotExist()
                );

        ArgumentCaptor<FindExactScientificFeatureVectorQuery>
                queryCaptor =
                ArgumentCaptor.forClass(
                        FindExactScientificFeatureVectorQuery.class
                );

        verify(queryUseCase)
                .findExact(
                        queryCaptor.capture()
                );

        FindExactScientificFeatureVectorQuery query =
                queryCaptor.getValue();

        assertThat(query.participantId())
                .isEqualTo(
                        new ParticipantId(PARTICIPANT_ID)
                );

        assertThat(query.featureSetVersion())
                .isEqualTo(
                        new FeatureSetVersion(
                                FEATURE_SET_VERSION
                        )
                );

        assertThat(query.featureCutoffAt())
                .isEqualTo(FEATURE_CUTOFF_AT);

        verifyNoMoreInteractions(queryUseCase);
    }

    @Test
    void shouldReturnHttp404WhenExactVectorIsAbsent()
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
                                        PARTICIPANT_ID
                                )
                                .param(
                                        "featureSetVersion",
                                        FEATURE_SET_VERSION
                                )
                                .param(
                                        "featureCutoffAt",
                                        FEATURE_CUTOFF_AT.toString()
                                )
                )
                .andExpect(status().isNotFound());

        verify(queryUseCase)
                .findExact(any());

        verifyNoMoreInteractions(queryUseCase);
    }

    @Test
    void shouldResolveLatestRouteBindPathAndSerializeJson()
            throws Exception {
        when(
                queryUseCase.findLatestCompleted(any())
        ).thenReturn(
                Optional.of(
                        result(
                                "VECTOR-LATEST-MOCKMVC-001"
                        )
                )
        );

        mockMvc.perform(
                        get(
                                BASE_PATH
                                        + "/latest-completed/"
                                        + PARTICIPANT_ID
                        )
                                .param(
                                        "featureSetVersion",
                                        FEATURE_SET_VERSION
                                )
                                .accept(
                                        MediaType.APPLICATION_JSON
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(
                        jsonPath("$.vectorId")
                                .value(
                                        "VECTOR-LATEST-MOCKMVC-001"
                                )
                )
                .andExpect(
                        jsonPath("$.participantId")
                                .value(PARTICIPANT_ID)
                )
                .andExpect(
                        jsonPath("$.features[0].featureCode")
                                .value("KOLB_CE")
                );

        ArgumentCaptor<FindLatestScientificFeatureVectorQuery>
                queryCaptor =
                ArgumentCaptor.forClass(
                        FindLatestScientificFeatureVectorQuery.class
                );

        verify(queryUseCase)
                .findLatestCompleted(
                        queryCaptor.capture()
                );

        FindLatestScientificFeatureVectorQuery query =
                queryCaptor.getValue();

        assertThat(query.participantId())
                .isEqualTo(
                        new ParticipantId(PARTICIPANT_ID)
                );

        assertThat(query.featureSetVersion())
                .isEqualTo(
                        new FeatureSetVersion(
                                FEATURE_SET_VERSION
                        )
                );

        verifyNoMoreInteractions(queryUseCase);
    }

    @Test
    void shouldReturnHttp404WhenLatestVectorIsAbsent()
            throws Exception {
        when(
                queryUseCase.findLatestCompleted(any())
        ).thenReturn(
                Optional.empty()
        );

        mockMvc.perform(
                        get(
                                BASE_PATH
                                        + "/latest-completed/"
                                        + PARTICIPANT_ID
                        )
                                .param(
                                        "featureSetVersion",
                                        FEATURE_SET_VERSION
                                )
                )
                .andExpect(status().isNotFound());

        verify(queryUseCase)
                .findLatestCompleted(any());

        verifyNoMoreInteractions(queryUseCase);
    }

    @Test
    void shouldReturnHttp400WhenExactParticipantIdIsMissing()
            throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/exact")
                                .param(
                                        "featureSetVersion",
                                        FEATURE_SET_VERSION
                                )
                                .param(
                                        "featureCutoffAt",
                                        FEATURE_CUTOFF_AT.toString()
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(queryUseCase);
    }

    @Test
    void shouldReturnHttp400WhenExactFeatureSetVersionIsMissing()
            throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/exact")
                                .param(
                                        "participantId",
                                        PARTICIPANT_ID
                                )
                                .param(
                                        "featureCutoffAt",
                                        FEATURE_CUTOFF_AT.toString()
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(queryUseCase);
    }

    @Test
    void shouldReturnHttp400WhenExactFeatureCutoffIsMissing()
            throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/exact")
                                .param(
                                        "participantId",
                                        PARTICIPANT_ID
                                )
                                .param(
                                        "featureSetVersion",
                                        FEATURE_SET_VERSION
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(queryUseCase);
    }

    @Test
    void shouldReturnHttp400WhenFeatureCutoffIsInvalid()
            throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/exact")
                                .param(
                                        "participantId",
                                        PARTICIPANT_ID
                                )
                                .param(
                                        "featureSetVersion",
                                        FEATURE_SET_VERSION
                                )
                                .param(
                                        "featureCutoffAt",
                                        "not-an-instant"
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(queryUseCase);
    }

    @Test
    void shouldReturnHttp400WhenLatestFeatureSetVersionIsMissing()
            throws Exception {
        mockMvc.perform(
                        get(
                                BASE_PATH
                                        + "/latest-completed/"
                                        + PARTICIPANT_ID
                        )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(queryUseCase);
    }

    @Test
    void shouldNotResolveUnknownScientificFeatureRoute()
            throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/unknown")
                )
                .andExpect(status().isNotFound());

        verifyNoInteractions(queryUseCase);
    }

    private ScientificFeatureVectorResult result(
            String vectorId
    ) {
        return new ScientificFeatureVectorResult(
                vectorId,
                PARTICIPANT_ID,
                FEATURE_SET_VERSION,
                "GENERATOR-V1",
                FEATURE_CUTOFF_AT,
                FEATURE_CUTOFF_AT.plusSeconds(5),
                1,
                "CHECKSUM-MOCKMVC-001",
                List.of(
                        ScientificFeatureItemResult.numeric(
                                "ITEM-MOCKMVC-001",
                                "KOLB_CE",
                                25.0,
                                "KOLB",
                                "ADMIN-MOCKMVC-001"
                        )
                )
        );
    }
}
