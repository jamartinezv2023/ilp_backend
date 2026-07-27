package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScientificChecksumTest {

    @Test
    void shouldNormalizeChecksum() {
        ScientificChecksum checksum =
                new ScientificChecksum(
                        "  SHA256-ABC123  "
                );

        assertThat(checksum.value())
                .isEqualTo("SHA256-ABC123");
    }

    @Test
    void shouldRejectBlankChecksum() {
        assertThatThrownBy(() ->
                new ScientificChecksum(" ")
        ).isInstanceOf(
                IllegalArgumentException.class
        );
    }

    @Test
    void shouldRejectOversizedChecksum() {
        String oversized =
                "X".repeat(129);

        assertThatThrownBy(() ->
                new ScientificChecksum(oversized)
        ).isInstanceOf(
                IllegalArgumentException.class
        );
    }
}