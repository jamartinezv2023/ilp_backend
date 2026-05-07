package com.inclusive.adaptiveeducationservice.domain.model;
import java.util.Map;

public class StudentCharacterization {
    private String studentId;
    private Map<String, Object> learningStyles;

    public StudentCharacterization() {}
    public StudentCharacterization(String studentId, Map<String, Object> learningStyles) {
        this.studentId = studentId;
        this.learningStyles = learningStyles;
    }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public Map<String, Object> getLearningStyles() { return learningStyles; }
    public void setLearningStyles(Map<String, Object> learningStyles) { this.learningStyles = learningStyles; }
}
