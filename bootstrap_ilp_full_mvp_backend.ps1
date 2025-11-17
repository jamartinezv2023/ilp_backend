$ErrorActionPreference = "Stop"

Write-Host "==============================================="
Write-Host " BOOTSTRAP ILP FULL MVP BACKEND (STUDENT CORE) "
Write-Host "==============================================="

# Base paths
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Write-Host "[INFO] Project root: $root"

$commonsDtoDir = Join-Path $root "commons-service\src\main\java\com\inclusive\common\dto"
$adaptiveRoot   = Join-Path $root "adaptive-education-service\src\main\java\com\inclusive\adaptiveeducationservice"

# Helper to ensure directories exist
function Ensure-Dir([string]$path) {
    if (!(Test-Path $path)) {
        New-Item -ItemType Directory -Path $path -Force | Out-Null
        Write-Host "[DIR] Created: $path"
    } else {
        Write-Host "[DIR] Exists:  $path"
    }
}

# Ensure base dirs
Ensure-Dir $commonsDtoDir
Ensure-Dir $adaptiveRoot

# Adaptive module subfolders
$adaptiveEntityDtoDir        = Join-Path $adaptiveRoot "entity\dto"
$adaptiveEntityModelDir      = Join-Path $adaptiveRoot "entity\model"
$adaptiveEntityRepoDir       = Join-Path $adaptiveRoot "entity\repository"
$adaptiveEntityServiceDir    = Join-Path $adaptiveRoot "entity\service"
$adaptiveEntityServiceImplDir= Join-Path $adaptiveEntityServiceDir "impl"
$adaptiveEntityControllerDir = Join-Path $adaptiveRoot "entity\controller"
$adaptiveConfigDir           = Join-Path $adaptiveRoot "config"
$adaptiveServiceDir          = Join-Path $adaptiveRoot "service"
$adaptiveControllerDir       = Join-Path $adaptiveRoot "controller"
$adaptiveClientDir           = Join-Path $adaptiveRoot "client"

Ensure-Dir $adaptiveEntityDtoDir
Ensure-Dir $adaptiveEntityModelDir
Ensure-Dir $adaptiveEntityRepoDir
Ensure-Dir $adaptiveEntityServiceDir
Ensure-Dir $adaptiveEntityServiceImplDir
Ensure-Dir $adaptiveEntityControllerDir
Ensure-Dir $adaptiveConfigDir
Ensure-Dir $adaptiveServiceDir
Ensure-Dir $adaptiveControllerDir
Ensure-Dir $adaptiveClientDir

Write-Host "==============================================="
Write-Host " STEP 1 - CLEAN OLD STUDENT* JAVA FILES"
Write-Host "==============================================="

if (Test-Path $adaptiveRoot) {
    $oldStudentFiles = Get-ChildItem -Path $adaptiveRoot -Recurse -Filter "*Student*.java"
    foreach ($f in $oldStudentFiles) {
        Write-Host "[DEL] $($f.FullName)"
        Remove-Item -Force $f.FullName
    }
} else {
    Write-Host "[WARN] Adaptive root not found: $adaptiveRoot"
}

Write-Host "==============================================="
Write-Host " STEP 2 - COMMONS-SERVICE StudentDTO"
Write-Host "==============================================="

$commonsStudentDtoPath = Join-Path $commonsDtoDir "StudentDTO.java"

@'
package com.inclusive.common.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Shared StudentDTO for cross-service communication.
 * This DTO is designed to be rich enough to support
 * accessibility, inclusion and machine learning use cases.
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
'@ | Set-Content -Path $commonsStudentDtoPath -Encoding Ascii

Write-Host "[OK]   Wrote: $commonsStudentDtoPath"

Write-Host "==============================================="
Write-Host " STEP 3 - ADAPTIVE StudentDTO (LOCAL)"
Write-Host "==============================================="

$adaptiveStudentDtoPath = Join-Path $adaptiveEntityDtoDir "StudentDTO.java"

@'
package com.inclusive.adaptiveeducationservice.entity.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Local StudentDTO for adaptive-education-service.
 * Aligned with accessibility, inclusion and ML-friendly fields.
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

    // Getters and setters (same as in commons, but local copy)

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
'@ | Set-Content -Path $adaptiveStudentDtoPath -Encoding Ascii

Write-Host "[OK]   Wrote: $adaptiveStudentDtoPath"

Write-Host "==============================================="
Write-Host " STEP 4 - Student ENTITY (JPA, ML READY) "
Write-Host "==============================================="

$adaptiveStudentEntityPath = Join-Path $adaptiveEntityModelDir "Student.java"

@'
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
'@ | Set-Content -Path $adaptiveStudentEntityPath -Encoding Ascii

Write-Host "[OK]   Wrote: $adaptiveStudentEntityPath"

Write-Host "==============================================="
Write-Host " STEP 5 - StudentRepository"
Write-Host "==============================================="

$adaptiveStudentRepoPath = Join-Path $adaptiveEntityRepoDir "StudentRepository.java"

@'
package com.inclusive.adaptiveeducationservice.entity.repository;

import com.inclusive.adaptiveeducationservice.entity.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    Optional<Student> findByEmail(String email);
}
'@ | Set-Content -Path $adaptiveStudentRepoPath -Encoding Ascii

Write-Host "[OK]   Wrote: $adaptiveStudentRepoPath"

Write-Host "==============================================="
Write-Host " STEP 6 - StudentMapper"
Write-Host "==============================================="

$adaptiveStudentMapperPath = Join-Path $adaptiveEntityModelDir "StudentMapper.java"

@'
package com.inclusive.adaptiveeducationservice.entity.model;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;

public final class StudentMapper {

    private StudentMapper() {
    }

    public static StudentDTO toDto(Student student) {
        if (student == null) {
            return null;
        }
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setUserId(student.getUserId());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setAge(student.getAge());
        dto.setGender(student.getGender());
        dto.setDisabilityStatus(student.getDisabilityStatus());
        dto.setNeedsAssistiveTechnology(student.getNeedsAssistiveTechnology());
        dto.setSchoolLevel(student.getSchoolLevel());
        dto.setSocioEconomicStatus(student.getSocioEconomicStatus());
        dto.setLocation(student.getLocation());
        dto.setFamilyStructure(student.getFamilyStructure());
        dto.setGuardianName(student.getGuardianName());
        dto.setEthnicity(student.getEthnicity());
        dto.setAttendanceRate(student.getAttendanceRate());
        dto.setAverageGrade(student.getAverageGrade());
        dto.setMathScore(student.getMathScore());
        dto.setReadingScore(student.getReadingScore());
        dto.setScienceScore(student.getScienceScore());
        dto.setHomeworkCompletionRate(student.getHomeworkCompletionRate());
        dto.setRepeatingGrade(student.getRepeatingGrade());
        dto.setBehavioralNotes(student.getBehavioralNotes());
        dto.setDisciplinaryActions(student.getDisciplinaryActions());
        dto.setLearningStyleFelder(student.getLearningStyleFelder());
        dto.setLearningStyleKolb(student.getLearningStyleKolb());
        dto.setVocationalProfileKuder(student.getVocationalProfileKuder());
        dto.setAdaptiveContentProfile(student.getAdaptiveContentProfile());
        dto.setEmotionalStateTrend(student.getEmotionalStateTrend());
        dto.setEngagementCluster(student.getEngagementCluster());
        dto.setPredictedDropoutRisk(student.getPredictedDropoutRisk());
        dto.setRecommendedLearningPath(student.getRecommendedLearningPath());
        dto.setDeviceAccess(student.getDeviceAccess());
        dto.setInternetAccess(student.getInternetAccess());
        dto.setPreferredStudyTime(student.getPreferredStudyTime());
        dto.setSiblingsInSchool(student.getSiblingsInSchool());
        dto.setLanguageSpokenAtHome(student.getLanguageSpokenAtHome());
        dto.setPreferredContentFormat(student.getPreferredContentFormat());
        dto.setReadingLevel(student.getReadingLevel());
        dto.setNumeracyLevel(student.getNumeracyLevel());
        dto.setAccountStatus(student.getAccountStatus());
        dto.setAvatarUrl(student.getAvatarUrl());
        dto.setBirthDate(student.getBirthDate());
        dto.setReceivesPsychologicalSupport(student.getReceivesPsychologicalSupport());
        dto.setReceivesSpecialEducationSupport(student.getReceivesSpecialEducationSupport());
        dto.setCreatedAt(student.getCreatedAt());
        dto.setUpdatedAt(student.getUpdatedAt());
        return dto;
    }

    public static Student toEntity(StudentDTO dto) {
        if (dto == null) {
            return null;
        }
        Student student = new Student();
        student.setId(dto.getId());
        student.setUserId(dto.getUserId());
        student.setFullName(dto.getFullName());
        student.setEmail(dto.getEmail());
        student.setAge(dto.getAge());
        student.setGender(dto.getGender());
        student.setDisabilityStatus(dto.getDisabilityStatus());
        student.setNeedsAssistiveTechnology(dto.getNeedsAssistiveTechnology());
        student.setSchoolLevel(dto.getSchoolLevel());
        student.setSocioEconomicStatus(dto.getSocioEconomicStatus());
        student.setLocation(dto.getLocation());
        student.setFamilyStructure(dto.getFamilyStructure());
        student.setGuardianName(dto.getGuardianName());
        student.setEthnicity(dto.getEthnicity());
        student.setAttendanceRate(dto.getAttendanceRate());
        student.setAverageGrade(dto.getAverageGrade());
        student.setMathScore(dto.getMathScore());
        student.setReadingScore(dto.getReadingScore());
        student.setScienceScore(dto.getScienceScore());
        student.setHomeworkCompletionRate(dto.getHomeworkCompletionRate());
        student.setRepeatingGrade(dto.getRepeatingGrade());
        student.setBehavioralNotes(dto.getBehavioralNotes());
        student.setDisciplinaryActions(dto.getDisciplinaryActions());
        student.setLearningStyleFelder(dto.getLearningStyleFelder());
        student.setLearningStyleKolb(dto.getLearningStyleKolb());
        student.setVocationalProfileKuder(dto.getVocationalProfileKuder());
        student.setAdaptiveContentProfile(dto.getAdaptiveContentProfile());
        student.setEmotionalStateTrend(dto.getEmotionalStateTrend());
        student.setEngagementCluster(dto.getEngagementCluster());
        student.setPredictedDropoutRisk(dto.getPredictedDropoutRisk());
        student.setRecommendedLearningPath(dto.getRecommendedLearningPath());
        student.setDeviceAccess(dto.getDeviceAccess());
        student.setInternetAccess(dto.getInternetAccess());
        student.setPreferredStudyTime(dto.getPreferredStudyTime());
        student.setSiblingsInSchool(dto.getSiblingsInSchool());
        student.setLanguageSpokenAtHome(dto.getLanguageSpokenAtHome());
        student.setPreferredContentFormat(dto.getPreferredContentFormat());
        student.setReadingLevel(dto.getReadingLevel());
        student.setNumeracyLevel(dto.getNumeracyLevel());
        student.setAccountStatus(dto.getAccountStatus());
        student.setAvatarUrl(dto.getAvatarUrl());
        student.setBirthDate(dto.getBirthDate());
        student.setReceivesPsychologicalSupport(dto.getReceivesPsychologicalSupport());
        student.setReceivesSpecialEducationSupport(dto.getReceivesSpecialEducationSupport());
        student.setCreatedAt(dto.getCreatedAt());
        student.setUpdatedAt(dto.getUpdatedAt());
        return student;
    }
}
'@ | Set-Content -Path $adaptiveStudentMapperPath -Encoding Ascii

Write-Host "[OK]   Wrote: $adaptiveStudentMapperPath"

Write-Host "==============================================="
Write-Host " STEP 7 - StudentService (domain service)"
Write-Host "==============================================="

$adaptiveStudentServicePath = Join-Path $adaptiveEntityServiceDir "StudentService.java"

@'
package com.inclusive.adaptiveeducationservice.entity.service;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    List<StudentDTO> findAll();

    Optional<StudentDTO> findById(Long id);

    StudentDTO create(StudentDTO dto);

    Optional<StudentDTO> update(Long id, StudentDTO dto);

    void delete(Long id);
}
'@ | Set-Content -Path $adaptiveStudentServicePath -Encoding Ascii

Write-Host "[OK]   Wrote: $adaptiveStudentServicePath"

Write-Host "==============================================="
Write-Host " STEP 8 - StudentServiceImpl"
Write-Host "==============================================="

$adaptiveStudentServiceImplPath = Join-Path $adaptiveEntityServiceImplDir "StudentServiceImpl.java"

@'
package com.inclusive.adaptiveeducationservice.entity.service.impl;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;
import com.inclusive.adaptiveeducationservice.entity.model.Student;
import com.inclusive.adaptiveeducationservice.entity.model.StudentMapper;
import com.inclusive.adaptiveeducationservice.entity.repository.StudentRepository;
import com.inclusive.adaptiveeducationservice.entity.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;

    public StudentServiceImpl(StudentRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentDTO> findById(Long id) {
        return repository.findById(id).map(StudentMapper::toDto);
    }

    @Override
    public StudentDTO create(StudentDTO dto) {
        if (dto.getEmail() != null && repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }
        Student entity = StudentMapper.toEntity(dto);
        if (entity.getAccountStatus() == null) {
            entity.setAccountStatus("ACTIVE");
        }
        OffsetDateTime now = OffsetDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        Student saved = repository.save(entity);
        return StudentMapper.toDto(saved);
    }

    @Override
    public Optional<StudentDTO> update(Long id, StudentDTO dto) {
        return repository.findById(id).map(existing -> {
            existing.setFullName(dto.getFullName());
            existing.setEmail(dto.getEmail());
            existing.setAge(dto.getAge());
            existing.setGender(dto.getGender());
            existing.setDisabilityStatus(dto.getDisabilityStatus());
            existing.setSchoolLevel(dto.getSchoolLevel());
            existing.setAttendanceRate(dto.getAttendanceRate());
            existing.setAverageGrade(dto.getAverageGrade());
            existing.setMathScore(dto.getMathScore());
            existing.setReadingScore(dto.getReadingScore());
            existing.setScienceScore(dto.getScienceScore());
            existing.setDeviceAccess(dto.getDeviceAccess());
            existing.setInternetAccess(dto.getInternetAccess());
            existing.setPreferredStudyTime(dto.getPreferredStudyTime());
            existing.setUpdatedAt(OffsetDateTime.now());
            Student saved = repository.save(existing);
            return StudentMapper.toDto(saved);
        });
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
'@ | Set-Content -Path $adaptiveStudentServiceImplPath -Encoding Ascii

Write-Host "[OK]   Wrote: $adaptiveStudentServiceImplPath"

Write-Host "==============================================="
Write-Host " STEP 9 - StudentController (REST CRUD)"
Write-Host "==============================================="

$adaptiveStudentControllerPath = Join-Path $adaptiveEntityControllerDir "StudentController.java"

@'
package com.inclusive.adaptiveeducationservice.entity.controller;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;
import com.inclusive.adaptiveeducationservice.entity.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public List<StudentDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StudentDTO> create(@RequestBody StudentDTO dto) {
        StudentDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> update(@PathVariable Long id, @RequestBody StudentDTO dto) {
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
'@ | Set-Content -Path $adaptiveStudentControllerPath -Encoding Ascii

Write-Host "[OK]   Wrote: $adaptiveStudentControllerPath"

Write-Host "==============================================="
Write-Host " STEP 10 - DataInitializer (sample data, ML-friendly)"
Write-Host "==============================================="

$adaptiveDataInitPath = Join-Path $adaptiveConfigDir "DataInitializer.java"

@'
package com.inclusive.adaptiveeducationservice.config;

import com.inclusive.adaptiveeducationservice.entity.model.Student;
import com.inclusive.adaptiveeducationservice.entity.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initStudents(StudentRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            OffsetDateTime now = OffsetDateTime.now();

            for (int i = 1; i <= 10; i++) {
                Student s = new Student();
                s.setFullName("Demo Student " + i);
                s.setEmail("student" + i + "@example.com");
                s.setAge(10 + i);
                s.setGender(i % 2 == 0 ? "F" : "M");
                s.setSchoolLevel("Secondary");
                s.setSocioEconomicStatus(i % 2 == 0 ? "Medium" : "Low");
                s.setAttendanceRate(0.9);
                s.setAverageGrade(4.0);
                s.setMathScore(4.2);
                s.setReadingScore(3.9);
                s.setScienceScore(4.1);
                s.setHomeworkCompletionRate(0.85);
                s.setDisabilityStatus(i % 3 == 0 ? "Learning" : "None");
                s.setNeedsAssistiveTechnology(i % 3 == 0);
                s.setAdaptiveContentProfile("standard");
                s.setEngagementCluster("medium");
                s.setPredictedDropoutRisk(0.1);
                s.setAccountStatus("ACTIVE");
                s.setBirthDate(LocalDate.now().minusYears(10 + i));
                s.setCreatedAt(now);
                s.setUpdatedAt(now);
                repository.save(s);
            }
        };
    }
}
'@ | Set-Content -Path $adaptiveDataInitPath -Encoding Ascii

Write-Host "[OK]   Wrote: $adaptiveDataInitPath"

Write-Host "==============================================="
Write-Host " STEP 11 - StudentIntegrationService (facade for SaaS/API gateway)"
Write-Host "==============================================="

$adaptiveIntegrationServicePath = Join-Path $adaptiveServiceDir "StudentIntegrationService.java"

@'
package com.inclusive.adaptiveeducationservice.service;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

public interface StudentIntegrationService {

    List<StudentDTO> getAll();

    Optional<StudentDTO> getById(Long id);

    StudentDTO create(StudentDTO dto);

    Optional<StudentDTO> update(Long id, StudentDTO dto);

    void delete(Long id);
}
'@ | Set-Content -Path $adaptiveIntegrationServicePath -Encoding Ascii

Write-Host "[OK]   Wrote: $adaptiveIntegrationServicePath"

Write-Host "==============================================="
Write-Host " STEP 12 - StudentIntegrationServiceImpl (delegates to domain)"
Write-Host "==============================================="

$adaptiveIntegrationServiceImplPath = Join-Path $adaptiveServiceDir "StudentIntegrationServiceImpl.java"

@'
package com.inclusive.adaptiveeducationservice.service;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;
import com.inclusive.adaptiveeducationservice.entity.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentIntegrationServiceImpl implements StudentIntegrationService {

    private final StudentService studentService;

    public StudentIntegrationServiceImpl(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public List<StudentDTO> getAll() {
        return studentService.findAll();
    }

    @Override
    public Optional<StudentDTO> getById(Long id) {
        return studentService.findById(id);
    }

    @Override
    public StudentDTO create(StudentDTO dto) {
        return studentService.create(dto);
    }

    @Override
    public Optional<StudentDTO> update(Long id, StudentDTO dto) {
        return studentService.update(id, dto);
    }

    @Override
    public void delete(Long id) {
        studentService.delete(id);
    }
}
'@ | Set-Content -Path $adaptiveIntegrationServiceImplPath -Encoding Ascii

Write-Host "[OK]   Wrote: $adaptiveIntegrationServiceImplPath"

Write-Host "==============================================="
Write-Host " STEP 13 - StudentProxyController (multi-tenant / proxy-ready)"
Write-Host "==============================================="

$adaptiveProxyControllerPath = Join-Path $adaptiveControllerDir "StudentProxyController.java"

@'
package com.inclusive.adaptiveeducationservice.controller;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;
import com.inclusive.adaptiveeducationservice.service.StudentIntegrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proxy/students")
public class StudentProxyController {

    private final StudentIntegrationService integrationService;

    public StudentProxyController(StudentIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @GetMapping
    public List<StudentDTO> getAll() {
        return integrationService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(@PathVariable Long id) {
        return integrationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StudentDTO> create(@RequestBody StudentDTO dto) {
        StudentDTO created = integrationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> update(@PathVariable Long id, @RequestBody StudentDTO dto) {
        return integrationService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        integrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
'@ | Set-Content -Path $adaptiveProxyControllerPath -Encoding Ascii

Write-Host "[OK]   Wrote: $adaptiveProxyControllerPath"

Write-Host "==============================================="
Write-Host " STEP 14 - StudentClient (stub for future external calls)"
Write-Host "==============================================="

$adaptiveClientPath = Join-Path $adaptiveClientDir "StudentClient.java"

@'
package com.inclusive.adaptiveeducationservice.client;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Stub client prepared for future cross-service calls.
 * Currently returns null to avoid coupling until endpoints are defined.
 */
@Component
public class StudentClient {

    private final RestTemplate restTemplate;

    public StudentClient() {
        this.restTemplate = new RestTemplate();
    }

    public StudentDTO fetchStudentById(Long id) {
        return null;
    }
}
'@ | Set-Content -Path $adaptiveClientPath -Encoding Ascii

Write-Host "[OK]   Wrote: $adaptiveClientPath"

Write-Host "==============================================="
Write-Host " BOOTSTRAP FULL MVP COMPLETED"
Write-Host " Next step: mvn clean package -pl adaptive-education-service -am"
Write-Host "==============================================="
