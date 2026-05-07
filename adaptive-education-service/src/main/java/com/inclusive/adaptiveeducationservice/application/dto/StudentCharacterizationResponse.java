package com.inclusive.adaptiveeducationservice.application.dto;

public class StudentCharacterizationResponse {

    private Long studentId;
    private String learningStyle;
    private String recommendedStrategy;

    public StudentCharacterizationResponse() {
    }

    public StudentCharacterizationResponse(
            Long studentId,
            String learningStyle,
            String recommendedStrategy
    ) {
        this.studentId = studentId;
        this.learningStyle = learningStyle;
        this.recommendedStrategy = recommendedStrategy;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getLearningStyle() {
        return learningStyle;
    }

    public String getRecommendedStrategy() {
        return recommendedStrategy;
    }
}