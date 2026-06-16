package com.inclusive.adaptiveeducationservice.instrument.service;

import com.inclusive.adaptiveeducationservice.instrument.entity.InstrumentQuestionEntity;
import com.inclusive.adaptiveeducationservice.instrument.repository.InstrumentQuestionRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InstrumentQuestionServiceTest {

    private final InstrumentQuestionRepository repository =
            mock(InstrumentQuestionRepository.class);

    private final InstrumentQuestionService service =
            new InstrumentQuestionService(repository);

    @Test
    void shouldReturnKolbQuestionsOrderedByQuestionOrder() {
        // Arrange
        when(repository.findByInstrumentOrderByQuestionOrderAsc("KOLB"))
                .thenReturn(List.of(new InstrumentQuestionEntity(
                        "KOLB-Q001",
                        "KOLB",
                        "CE",
                        1,
                        "Question text",
                        List.of("1", "2", "3", "4"),
                        "KOLB_BASELINE_V1"
                )));

        // Act
        var questions = service.findKolbQuestions();

        // Assert
        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).instrument()).isEqualTo("KOLB");
        assertThat(questions.get(0).options()).containsExactly(
                "1",
                "2",
                "3",
                "4"
        );
    }
}