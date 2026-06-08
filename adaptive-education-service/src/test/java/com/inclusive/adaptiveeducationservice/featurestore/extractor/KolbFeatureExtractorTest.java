package com.inclusive.adaptiveeducationservice.featurestore.extractor;

import com.inclusive.adaptiveeducationservice.assessmentresponse.entity.AssessmentAnswerEntity;
import com.inclusive.adaptiveeducationservice.assessmentresponse.entity.AssessmentResponseEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KolbFeatureExtractorTest {

    private final KolbFeatureExtractor extractor = new KolbFeatureExtractor();

    @Test
    void shouldExtractKolbFeaturesFromAssessmentResponses() {
        // Arrange
        var response = new AssessmentResponseEntity(
                "AR-001",
                "ST-001",
                "KOLB_V1",
                "1.0",
                "SUBMITTED",
                Instant.now()
        );

        response.addAnswer(new AssessmentAnswerEntity(
                "AR-001-A001",
                "KOLB-V1-Q001",
                "KOLB-V1-Q001-AC",
                "AC",
                "AC",
                4
        ));

        response.addAnswer(new AssessmentAnswerEntity(
                "AR-001-A002",
                "KOLB-V1-Q001",
                "KOLB-V1-Q001-AE",
                "AE",
                "AE",
                3
        ));

        response.addAnswer(new AssessmentAnswerEntity(
                "AR-001-A003",
                "KOLB-V1-Q001",
                "KOLB-V1-Q001-CE",
                "CE",
                "CE",
                1
        ));

        response.addAnswer(new AssessmentAnswerEntity(
                "AR-001-A004",
                "KOLB-V1-Q001",
                "KOLB-V1-Q001-RO",
                "RO",
                "RO",
                2
        ));

        // Act
        var features = extractor.extract(List.of(response));

        // Assert
        assertThat(features.ac()).isEqualTo(4);
        assertThat(features.ae()).isEqualTo(3);
        assertThat(features.ce()).isEqualTo(1);
        assertThat(features.ro()).isEqualTo(2);
        assertThat(features.style()).isEqualTo("CONVERGENT");
    }
}