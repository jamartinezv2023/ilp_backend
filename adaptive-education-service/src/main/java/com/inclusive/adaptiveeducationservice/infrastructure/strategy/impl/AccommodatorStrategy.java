package com.inclusive.adaptiveeducationservice.infrastructure.strategy.impl;
import com.inclusive.adaptiveeducationservice.infrastructure.strategy.InferenceStrategy;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class AccommodatorStrategy implements InferenceStrategy {
    @Override
    public boolean appliesTo(Map<String, Object> styles) {
        Object style = styles.get("kolb");
        return style != null && style.toString().equalsIgnoreCase("ACCOMMODATOR");
    }
    @Override
    public String getStyleKey() { return "ACCOMMODATOR"; }
}
