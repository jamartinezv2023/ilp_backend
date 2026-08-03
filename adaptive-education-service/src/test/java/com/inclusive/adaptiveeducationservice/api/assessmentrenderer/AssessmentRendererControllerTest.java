package com.inclusive.adaptiveeducationservice.api.assessmentrenderer;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.GenericAssessmentDefinitionQueryService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.AssessmentDefinitionNotFoundException;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata.AssessmentInstrumentType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata.AssessmentMetadata;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.rendering.AssessmentRendererModel;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.rendering.AssessmentRendererOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.rendering.AssessmentRendererQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AssessmentRendererControllerTest {

    private GenericAssessmentDefinitionQueryService queryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        queryService =
                mock(
                        GenericAssessmentDefinitionQueryService.class
                );

        AssessmentRendererController controller =
                new AssessmentRendererController(
                        queryService
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(
                        new AssessmentRendererExceptionHandler()
                )
                .build();
    }

    @Test
    void shouldReturnGenericRendererModel()
            throws Exception {
        when(
                queryService.findRendererModel("TEST_V1")
        ).thenReturn(model());

        mockMvc.perform(
                        get(
                                "/api/v1/assessment-renderer/"
                                        + "TEST_V1"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.code")
                                .value("TEST_V1")
                )
                .andExpect(
                        jsonPath("$.version")
                                .value("1.0")
                )
                .andExpect(
                        jsonPath(
                                "$.questions[0].questionType"
                        ).value("SINGLE_CHOICE")
                )
                .andExpect(
                        jsonPath(
                                "$.questions[0].options[0].code"
                        ).value("A")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownCode()
            throws Exception {
        when(
                queryService.findRendererModel("UNKNOWN")
        ).thenThrow(
                new AssessmentDefinitionNotFoundException(
                        "UNKNOWN"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/assessment-renderer/"
                                        + "UNKNOWN"
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/v1/"
                                                + "assessment-renderer/"
                                                + "UNKNOWN"
                                )
                );
    }

    @Test
    void shouldReturnAllActiveRendererModels()
            throws Exception {
        when(
                queryService.findAllActiveRendererModels()
        ).thenReturn(List.of(model()));

        mockMvc.perform(
                        get(
                                "/api/v1/assessment-renderer"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].code")
                                .value("TEST_V1")
                );
    }

    private AssessmentRendererModel model() {
        AssessmentMetadata metadata =
                new AssessmentMetadata(
                        "TEST_V1",
                        "Generic test",
                        "ILP",
                        "1.0",
                        AssessmentInstrumentType.CUSTOM,
                        "es",
                        15,
                        "Renderer test",
                        "Institutional use"
                );

        AssessmentRendererOption option =
                new AssessmentRendererOption(
                        "OPTION-A",
                        "A",
                        "Option A",
                        "GENERAL",
                        1.0,
                        1.0,
                        1
                );

        AssessmentRendererQuestion question =
                new AssessmentRendererQuestion(
                        "QUESTION-1",
                        "Q1",
                        "Question one",
                        "GENERAL",
                        "SINGLE_CHOICE",
                        true,
                        1,
                        List.of(option)
                );

        return new AssessmentRendererModel(
                "TEST_V1",
                "1.0",
                "Generic test",
                "Renderer test",
                "Complete the test.",
                metadata,
                List.of(question)
        );
    }
}