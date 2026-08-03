package com.inclusive.adaptiveeducationservice.featurestore.scientific.configuration;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.query.ScientificFeatureVectorQueryService;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.query.mapper.ScientificFeatureVectorResultMapper;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.ScientificFeatureVectorQueryUseCase;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureVectorQueryPort;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ScientificFeatureQueryConfigurationTest {

    private final ScientificFeatureQueryConfiguration configuration =
            new ScientificFeatureQueryConfiguration();

    @Test
    void shouldCreateScientificFeatureVectorResultMapper() {
        ScientificFeatureVectorResultMapper mapper =
                configuration
                        .scientificFeatureVectorResultMapper();

        assertThat(mapper)
                .isNotNull();
    }

    @Test
    void shouldCreateScientificFeatureVectorQueryUseCase() {
        ScientificFeatureVectorQueryPort queryPort =
                mock(
                        ScientificFeatureVectorQueryPort.class
                );

        ScientificFeatureVectorResultMapper mapper =
                new ScientificFeatureVectorResultMapper();

        ScientificFeatureVectorQueryUseCase useCase =
                configuration
                        .scientificFeatureVectorQueryUseCase(
                                queryPort,
                                mapper
                        );

        assertThat(useCase)
                .isNotNull()
                .isInstanceOf(
                        ScientificFeatureVectorQueryService.class
                );
    }
}
