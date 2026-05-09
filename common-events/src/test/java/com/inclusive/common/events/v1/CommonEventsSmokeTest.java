package com.inclusive.common.events.v1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommonEventsSmokeTest {

    @Test
    void shouldLoadAssessmentCompletedEventClass() {
        assertNotNull(AssessmentCompletedEvent.class);
    }

    @Test
    void shouldLoadLearningPathInferenceRequestedEventClass() {
        assertNotNull(LearningPathInferenceRequestedEvent.class);
    }

    @Test
    void shouldLoadLearningPathRecommendedEventClass() {
        assertNotNull(LearningPathRecommendedEvent.class);
    }
}
