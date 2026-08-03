package com.inclusive.adaptiveeducationservice.featurestore.scientific.configuration;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.query.ScientificFeatureVectorQueryService;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.query.mapper.ScientificFeatureVectorResultMapper;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.ScientificFeatureVectorQueryUseCase;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureVectorQueryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScientificFeatureQueryConfiguration {

    @Bean
    public ScientificFeatureVectorResultMapper
    scientificFeatureVectorResultMapper() {
        return new ScientificFeatureVectorResultMapper();
    }

    @Bean
    public ScientificFeatureVectorQueryUseCase
    scientificFeatureVectorQueryUseCase(
            ScientificFeatureVectorQueryPort queryPort,
            ScientificFeatureVectorResultMapper resultMapper
    ) {
        return new ScientificFeatureVectorQueryService(
                queryPort,
                resultMapper
        );
    }
}
