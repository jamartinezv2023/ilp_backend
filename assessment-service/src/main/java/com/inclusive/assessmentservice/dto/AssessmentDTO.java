package com.inclusive.assessmentservice.dto;

public class AssessmentDTO {

    private Long id;
    private Long userId;
    private String type;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getType() { return type; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setType(String type) { this.type = type; }
}