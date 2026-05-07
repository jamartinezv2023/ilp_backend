package com.inclusive.adaptiveeducationservice.infrastructure.strategy;
import java.util.Map;

public interface InferenceStrategy {
    boolean appliesTo(Map<String, Object> styles);
    String getStyleKey();
}
