package com.inclusive.common.events.v1;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathRecommendedEvent {
    private String tenantId;
    private Long studentId;
    private String title;
    private String content;
    private String adjustmentType;
    private List<String> recommendedActivities;
    private Double score;
    private String result;
    private String correlationId;
    private String type;
    private Map<String, String> metadata;

    // Método estático factory solicitado por el servicio
    public static LearningPathRecommendedEvent of(String tenantId, Long studentId, String title, String content, 
            String adjustmentType, List<String> activities, Double score, String result, String correlationId, 
            String type, Map<String, String> metadata) {
        return new LearningPathRecommendedEvent(tenantId, studentId, title, content, adjustmentType, 
                activities, score, result, correlationId, type, metadata);
    }
}
