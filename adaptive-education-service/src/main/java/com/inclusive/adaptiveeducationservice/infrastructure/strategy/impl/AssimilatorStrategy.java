package com.inclusive.adaptiveeducationservice.infrastructure.strategy.impl;
import com.inclusive.adaptiveeducationservice.infrastructure.strategy.InferenceStrategy;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class AssimilatorStrategy implements InferenceStrategy {
    @Override
    public boolean appliesTo(Map<String, Object> styles) {
        Object style = styles.get("kolb");
        return style != null && style.toString().equalsIgnoreCase("ASSIMILATOR");
    }
    @Override
    public String getStyleKey() { return "ASSIMILATOR"; }
}
