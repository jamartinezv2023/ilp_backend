package com.inclusive.adaptiveeducationservice.entity.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Local StudentDTO used inside adaptive-education-service.
 * It mirrors the shared DTO structure to keep mapping simple
 * and ML / accessibility friendly.
 */
public class StudentDTO implements Serializable {

    private Long id;
    private Long userId;

    private String fullName;
    private String email;
    private Integer age;
    private String gender;

    private String disabilityStatus;
    private Boolean needsAssistiveTechnology;

    private Boolean receivesPsychologicalSupport;
    private Boolean receivesSpecialEducationSupport;

    private String schoolLevel;
    private String socioEconomicStatus;
    private String location;
    private String familyStructure;
    private String guardianName;
    private String ethnicity;

    private Double attendanceRate;
    private Double averageGrade;
    private Double mathScore;
    private Double readingScore;
    private Double scienceScore;
    private Double homeworkCompletionRate;
    private Boolean repeatingGrade;
    private String behavioralNotes;
    private String disciplinaryActions;

    private String learningStyleFelder;
    private String learningStyleKolb;
    private String vocationalProfileKuder;

    private String adaptiveContentProfile;
    private String emotionalStateTrend;
    private String engagementCluster;
    private Double predictedDropoutRisk;
    private String recommendedLearningPath;

    private String deviceAccess;
    private String internetAccess;

    private String preferredStudyTime;
    private Integer siblingsInSchool;
    private String languageSpokenAtHome;
    private String preferredContentFormat;
    private String readingLevel;
    private String numeracyLevel;

    private String accountStatus;
    private String avatarUrl;
    private LocalDate birthDate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public StudentDTO() {
    }

    // Getters & setters (mismos nombres que en commons)

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
