package com.inclusive.adaptiveeducationservice.domain.adaptation;
import jakarta.persistence.*;

@Entity
@Table(name = "learning_recommendations")
public class LearningRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;
    private String content;

    public LearningRecommendation() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
