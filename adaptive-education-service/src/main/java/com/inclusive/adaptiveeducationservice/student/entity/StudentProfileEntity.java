package com.inclusive.adaptiveeducationservice.student.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student_profiles")
public class StudentProfileEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String learningProfile;

    @Column(nullable = false)
    private String vocationalInterest;

    @Column(nullable = false)
    private String supportLevel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "student_inclusive_strategies",
            joinColumns = @JoinColumn(name = "student_id")
    )
    @Column(name = "strategy", nullable = false, length = 1000)
    private List<String> inclusiveStrategies = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "student_pedagogical_recommendations",
            joinColumns = @JoinColumn(name = "student_id")
    )
    @Column(name = "recommendation", nullable = false, length = 1000)
    private List<String> pedagogicalRecommendations = new ArrayList<>();

    protected StudentProfileEntity() {
    }

    public StudentProfileEntity(
            String id,
            String fullName,
            String grade,
            Integer age,
            String learningProfile,
            String vocationalInterest,
            String supportLevel,
            List<String> inclusiveStrategies,
            List<String> pedagogicalRecommendations
    ) {
        this.id = id;
        this.fullName = fullName;
        this.grade = grade;
        this.age = age;
        this.learningProfile = learningProfile;
        this.vocationalInterest = vocationalInterest;
        this.supportLevel = supportLevel;
        this.inclusiveStrategies = new ArrayList<>(inclusiveStrategies);
        this.pedagogicalRecommendations =
                new ArrayList<>(pedagogicalRecommendations);
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGrade() {
        return grade;
    }

    public Integer getAge() {
        return age;
    }

    public String getLearningProfile() {
        return learningProfile;
    }

    public String getVocationalInterest() {
        return vocationalInterest;
    }

    public String getSupportLevel() {
        return supportLevel;
    }

    public List<String> getInclusiveStrategies() {
        return List.copyOf(inclusiveStrategies);
    }

    public List<String> getPedagogicalRecommendations() {
        return List.copyOf(pedagogicalRecommendations);
    }

    public void updateLearningProfile(String learningProfile) {
        this.learningProfile = learningProfile;
    }

    public void updateProfile(
            String fullName,
            String grade,
            Integer age,
            String learningProfile,
            String vocationalInterest,
            String supportLevel,
            List<String> inclusiveStrategies,
            List<String> pedagogicalRecommendations
    ) {
        this.fullName = fullName;
        this.grade = grade;
        this.age = age;
        this.learningProfile = learningProfile;
        this.vocationalInterest = vocationalInterest;
        this.supportLevel = supportLevel;
        this.inclusiveStrategies = new ArrayList<>(inclusiveStrategies);
        this.pedagogicalRecommendations =
                new ArrayList<>(pedagogicalRecommendations);
    }
}