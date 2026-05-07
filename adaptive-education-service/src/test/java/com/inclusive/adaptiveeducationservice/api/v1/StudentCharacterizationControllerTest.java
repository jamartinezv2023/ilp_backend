package com.inclusive.adaptiveeducationservice.api.v1;

import com.inclusive.adaptiveeducationservice.application.ports.input.CharacterizeStudentUseCase;
import com.inclusive.adaptiveeducationservice.domain.model.StudentCharacterization;
import com.inclusive.adaptiveeducationservice.mapper.StudentCharacterizationMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentCharacterizationController.class)
class StudentCharacterizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CharacterizeStudentUseCase characterizeStudentUseCase;

    @MockBean
    private StudentCharacterizationMapper mapper;

    @Test
    void shouldReturnOkWhenCharacterizationExists() throws Exception {

        // Arrange
        Long studentId = 1L;

        Mockito.when(
                characterizeStudentUseCase.characterizeStudent(studentId)
        ).thenReturn(new StudentCharacterization());

        // Act & Assert
        mockMvc.perform(
                        get("/api/v1/characterizations/{studentId}", studentId)
                )
                .andExpect(status().isOk());
    }
}