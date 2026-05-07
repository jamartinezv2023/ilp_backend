package com.inclusive.adaptiveeducationservice.infrastructure.strategy.impl;
import com.inclusive.adaptiveeducationservice.domain.model.StudentCharacterization;
import com.inclusive.adaptiveeducationservice.infrastructure.strategy.InferenceStrategy;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class VisualInclusionStrategy implements InferenceStrategy {
    @Override
    public boolean appliesTo(Map<String, Object> styles) { return false; }
    @Override
    public String getStyleKey() { return "VISUAL"; }
}
