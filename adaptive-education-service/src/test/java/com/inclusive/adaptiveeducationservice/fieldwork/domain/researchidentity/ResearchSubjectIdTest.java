package com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResearchSubjectIdTest {

    @Test
    void shouldCreateResearchSubjectIdFromUuid() {
        UUID value = UUID.randomUUID();

        ResearchSubjectId researchSubjectId =
                new ResearchSubjectId(value);

        assertThat(researchSubjectId.value())
                .isEqualTo(value);
    }

    @Test
    void shouldRejectNullValue() {
        assertThatThrownBy(
                () -> new ResearchSubjectId(null)
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessage(
                        "value is required"
                );
    }

    @Test
    void shouldGenerateNewResearchSubjectId() {
        ResearchSubjectId researchSubjectId =
                ResearchSubjectId.generate();

        assertThat(researchSubjectId)
                .isNotNull();

        assertThat(researchSubjectId.value())
                .isNotNull();
    }

    @Test
    void shouldGenerateDifferentResearchSubjectIds() {
        ResearchSubjectId first =
                ResearchSubjectId.generate();

        ResearchSubjectId second =
                ResearchSubjectId.generate();

        assertThat(first)
                .isNotEqualTo(second);
    }
}
