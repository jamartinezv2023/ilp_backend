package com.inclusive.adaptiveeducationservice.infrastructure.strategy.impl;

import com.inclusive.adaptiveeducationservice.infrastructure.strategy.InferenceStrategy;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ConvergentStrategy implements InferenceStrategy {
    @Override
    public boolean appliesTo(Map<String, Object> styles) {
        // Buscamos "CONVERGENT" ignorando mayúsculas/minúsculas
        Object style = styles.get("kolb");
        return style != null && style.toString().equalsIgnoreCase("CONVERGENT");
    }

    @Override
    public String getStyleKey() {
        return "CONVERGENT";
    }
}
