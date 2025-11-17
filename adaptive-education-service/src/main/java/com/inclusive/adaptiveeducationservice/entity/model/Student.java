package com.inclusive.adaptiveeducationservice.entity.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Student entity mapped to students table.
 * Includes fields relevant for accessibility, inclusion and ML.
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender", length = 32)
    private String gender;

    @Column(name = "disability_status", length = 128)
    private String disabilityStatus;

    @Column(name = "needs_assistive_technology")
    private Boolean needsAssistiveTechnology;

    @Column(name = "school_level", length = 64)
    private String schoolLevel;

    @Column(name = "socio_economic_status", length = 64)
    private String socioEconomicStatus;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "family_structure", length = 128)
    private String familyStructure;

    @Column(name = "guardian_name", length = 255)
    private String guardianName;

    @Column(name = "ethnicity", length = 64)
    private String ethnicity;

    @Column(name = "attendance_rate")
    private Double attendanceRate;

    @Column(name = "average_grade")
    private Double averageGrade;

    @Column(name = "math_score")
    private Double mathScore;

    @Column(name = "reading_score")
    private Double readingScore;

    @Column(name = "science_score")
    private Double scienceScore;

    @Column(name = "homework_completion_rate")
    private Double homeworkCompletionRate;

    @Column(name = "is_repeating_grade")
    private Boolean repeatingGrade;

    @Column(name = "behavioral_notes", columnDefinition = "text")
    private String behavioralNotes;

    @Column(name = "disciplinary_actions", columnDefinition = "text")
    private String disciplinaryActions;

    @Column(name = "learning_style_felder", length = 64)
    private String learningStyleFelder;

    @Column(name = "learning_style_kolb", length = 64)
    private String learningStyleKolb;

    @Column(name = "vocational_profile_kuder", length = 64)
    private String vocationalProfileKuder;

    @Column(name = "adaptive_content_profile", columnDefinition = "text")
    private String adaptiveContentProfile;

    @Column(name = "emotional_state_trend", columnDefinition = "text")
    private String emotionalStateTrend;

    @Column(name = "engagement_cluster", length = 64)
    private String engagementCluster;

    @Column(name = "predicted_dropout_risk")
    private Double predictedDropoutRisk;

    @Column(name = "recommended_learning_path", columnDefinition = "text")
    private String recommendedLearningPath;

    @Column(name = "device_access", length = 64)
    private String deviceAccess;

    @Column(name = "internet_access", length = 64)
    private String internetAccess;

    @Column(name = "preferred_study_time", length = 64)
    private String preferredStudyTime;

    @Column(name = "siblings_in_school")
    private Integer siblingsInSchool;

    @Column(name = "language_spoken_at_home", length = 64)
    private String languageSpokenAtHome;

    @Column(name = "preferred_content_format", length = 64)
    private String preferredContentFormat;

    @Column(name = "reading_level", length = 64)
    private String readingLevel;

    @Column(name = "numeracy_level", length = 64)
    private String numeracyLevel;

    @Column(name = "account_status", length = 32)
    private String accountStatus;

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "receives_psychological_support")
    private Boolean receivesPsychologicalSupport;

    @Column(name = "receives_special_education_support")
    private Boolean receivesSpecialEducationSupport;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Student() {
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDisabilityStatus() {
        return disabilityStatus;
    }

    public void setDisabilityStatus(String disabilityStatus) {
        this.disabilityStatus = disabilityStatus;
    }

    public Boolean getNeedsAssistiveTechnology() {
        return needsAssistiveTechnology;
    }

    public void setNeedsAssistiveTechnology(Boolean needsAssistiveTechnology) {
        this.needsAssistiveTechnology = needsAssistiveTechnology;
    }

    public String getSchoolLevel() {
        return schoolLevel;
    }

    public void setSchoolLevel(String schoolLevel) {
        this.schoolLevel = schoolLevel;
    }

    public String getSocioEconomicStatus() {
        return socioEconomicStatus;
    }

    public void setSocioEconomicStatus(String socioEconomicStatus) {
        this.socioEconomicStatus = socioEconomicStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFamilyStructure() {
        return familyStructure;
    }

    public void setFamilyStructure(String familyStructure) {
        this.familyStructure = familyStructure;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public Double getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(Double attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public Double getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(Double averageGrade) {
        this.averageGrade = averageGrade;
    }

    public Double getMathScore() {
        return mathScore;
    }

    public void setMathScore(Double mathScore) {
        this.mathScore = mathScore;
    }

    public Double getReadingScore() {
        return readingScore;
    }

    public void setReadingScore(Double readingScore) {
        this.readingScore = readingScore;
    }

    public Double getScienceScore() {
        return scienceScore;
    }

    public void setScienceScore(Double scienceScore) {
        this.scienceScore = scienceScore;
    }

    public Double getHomeworkCompletionRate() {
        return homeworkCompletionRate;
    }

    public void setHomeworkCompletionRate(Double homeworkCompletionRate) {
        this.homeworkCompletionRate = homeworkCompletionRate;
    }

    public Boolean getRepeatingGrade() {
        return repeatingGrade;
    }

    public void setRepeatingGrade(Boolean repeatingGrade) {
        this.repeatingGrade = repeatingGrade;
    }

    public String getBehavioralNotes() {
        return behavioralNotes;
    }

    public void setBehavioralNotes(String behavioralNotes) {
        this.behavioralNotes = behavioralNotes;
    }

    public String getDisciplinaryActions() {
        return disciplinaryActions;
    }

    public void setDisciplinaryActions(String disciplinaryActions) {
        this.disciplinaryActions = disciplinaryActions;
    }

    public String getLearningStyleFelder() {
        return learningStyleFelder;
    }

    public void setLearningStyleFelder(String learningStyleFelder) {
        this.learningStyleFelder = learningStyleFelder;
    }

    public String getLearningStyleKolb() {
        return learningStyleKolb;
    }

    public void setLearningStyleKolb(String learningStyleKolb) {
        this.learningStyleKolb = learningStyleKolb;
    }

    public String getVocationalProfileKuder() {
        return vocationalProfileKuder;
    }

    public void setVocationalProfileKuder(String vocationalProfileKuder) {
        this.vocationalProfileKuder = vocationalProfileKuder;
    }

    public String getAdaptiveContentProfile() {
        return adaptiveContentProfile;
    }

    public void setAdaptiveContentProfile(String adaptiveContentProfile) {
        this.adaptiveContentProfile = adaptiveContentProfile;
    }

    public String getEmotionalStateTrend() {
        return emotionalStateTrend;
    }

    public void setEmotionalStateTrend(String emotionalStateTrend) {
        this.emotionalStateTrend = emotionalStateTrend;
    }

    public String getEngagementCluster() {
        return engagementCluster;
    }

    public void setEngagementCluster(String engagementCluster) {
        this.engagementCluster = engagementCluster;
    }

    public Double getPredictedDropoutRisk() {
        return predictedDropoutRisk;
    }

    public void setPredictedDropoutRisk(Double predictedDropoutRisk) {
        this.predictedDropoutRisk = predictedDropoutRisk;
    }

    public String getRecommendedLearningPath() {
        return recommendedLearningPath;
    }

    public void setRecommendedLearningPath(String recommendedLearningPath) {
        this.recommendedLearningPath = recommendedLearningPath;
    }

    public String getDeviceAccess() {
        return deviceAccess;
    }

    public void setDeviceAccess(String deviceAccess) {
        this.deviceAccess = deviceAccess;
    }

    public String getInternetAccess() {
        return internetAccess;
    }

    public void setInternetAccess(String internetAccess) {
        this.internetAccess = internetAccess;
    }

    public String getPreferredStudyTime() {
        return preferredStudyTime;
    }

    public void setPreferredStudyTime(String preferredStudyTime) {
        this.preferredStudyTime = preferredStudyTime;
    }

    public Integer getSiblingsInSchool() {
        return siblingsInSchool;
    }

    public void setSiblingsInSchool(Integer siblingsInSchool) {
        this.siblingsInSchool = siblingsInSchool;
    }

    public String getLanguageSpokenAtHome() {
        return languageSpokenAtHome;
    }

    public void setLanguageSpokenAtHome(String languageSpokenAtHome) {
        this.languageSpokenAtHome = languageSpokenAtHome;
    }

    public String getPreferredContentFormat() {
        return preferredContentFormat;
    }

    public void setPreferredContentFormat(String preferredContentFormat) {
        this.preferredContentFormat = preferredContentFormat;
    }

    public String getReadingLevel() {
        return readingLevel;
    }

    public void setReadingLevel(String readingLevel) {
        this.readingLevel = readingLevel;
    }

    public String getNumeracyLevel() {
        return numeracyLevel;
    }

    public void setNumeracyLevel(String numeracyLevel) {
        this.numeracyLevel = numeracyLevel;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Boolean getReceivesPsychologicalSupport() {
        return receivesPsychologicalSupport;
    }

    public void setReceivesPsychologicalSupport(Boolean receivesPsychologicalSupport) {
        this.receivesPsychologicalSupport = receivesPsychologicalSupport;
    }

    public Boolean getReceivesSpecialEducationSupport() {
        return receivesSpecialEducationSupport;
    }

    public void setReceivesSpecialEducationSupport(Boolean receivesSpecialEducationSupport) {
        this.receivesSpecialEducationSupport = receivesSpecialEducationSupport;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
