package com.inclusive.adaptiveeducationservice.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdaptationRuleRequestTest {

    @Test
    void shouldBeEnabledByDefault() {
        AdaptationRuleRequest request =
                new AdaptationRuleRequest();

        assertTrue(request.isEnabled());
    }

    @Test
    void shouldUpdateEnabledFlag() {
        AdaptationRuleRequest request =
                new AdaptationRuleRequest();

        request.setEnabled(false);

        assertFalse(request.isEnabled());
    }
}
