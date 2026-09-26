package com.inclusive.adaptiveeducationservice.api.assessment;

import com.inclusive.adaptiveeducationservice.assessment.service.FelderSilvermanAssessmentService;
import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentService;
import com.inclusive.adaptiveeducationservice.assessment.service.KuderAssessmentService;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class LegacyAssessmentWriteGateTest {

    private final LegacyAssessmentWritePolicy policy = new LegacyAssessmentWritePolicy();
    private final KolbAssessmentService kolb = mock(KolbAssessmentService.class);
    private final FelderSilvermanAssessmentService felder =
            mock(FelderSilvermanAssessmentService.class);
    private final KuderAssessmentService kuder = mock(KuderAssessmentService.class);

    @Test
    void legacyPostMethodsRejectBeforeCallingTheirServices() {
        assertGone(() -> new KolbAssessmentController(kolb, policy).submit(null));
        assertGone(() -> new FelderSilvermanAssessmentController(felder, policy).submit(null));
        assertGone(() -> new KuderAssessmentController(kuder, policy).submit(null));
        verifyNoInteractions(kolb, felder, kuder);
    }

    private void assertGone(org.assertj.core.api.ThrowableAssert.ThrowingCallable call) {
        assertThatThrownBy(call)
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(
                        ((ResponseStatusException) error).getStatusCode().value()
                ).isEqualTo(410));
    }
}
