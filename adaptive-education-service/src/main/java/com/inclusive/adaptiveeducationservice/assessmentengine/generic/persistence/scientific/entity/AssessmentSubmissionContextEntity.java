package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "assessment_submission_context")
public class AssessmentSubmissionContextEntity {

    @Id
    @Column(length = 255)
    private String id;

    @Column(
            name = "administration_id",
            nullable = false,
            unique = true,
            length = 255
    )
    private String administrationId;

    @Column(name = "institution_id")
    private String institutionId;

    @Column(name = "campus_id")
    private String campusId;

    @Column(name = "program_id")
    private String programId;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "cohort_id")
    private String cohortId;

    @Column(name = "teacher_id")
    private String teacherId;

    @Column(length = 100)
    private String grade;

    @Column(
            name = "academic_year",
            length = 20
    )
    private String academicYear;

    @Column(
            name = "academic_period",
            length = 100
    )
    private String academicPeriod;

    @Column(
            name = "fieldwork_phase",
            length = 100
    )
    private String fieldworkPhase;

    @Column(name = "intervention_id")
    private String interventionId;

    @Column(
            name = "intervention_group",
            length = 100
    )
    private String interventionGroup;

    @Column(length = 100)
    private String source;

    @Column(
            name = "delivery_mode",
            length = 100
    )
    private String deliveryMode;

    @Column(length = 20)
    private String language;

    @Column(
            name = "device_type",
            length = 100
    )
    private String deviceType;

    private String browser;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(length = 100)
    private String timezone;

    @Column(
            name = "application_version",
            length = 100
    )
    private String applicationVersion;

    @Column(name = "consent_id")
    private String consentId;

    @Column(
            name = "consent_version",
            length = 100
    )
    private String consentVersion;

    @Column(name = "ethics_protocol")
    private String ethicsProtocol;

    @Column(
            name = "feature_set_version",
            length = 100
    )
    private String featureSetVersion;

    @Column(
            name = "preprocessing_version",
            length = 100
    )
    private String preprocessingVersion;

    @Column(
            name = "normalization_version",
            length = 100
    )
    private String normalizationVersion;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Column(
            name = "feature_cutoff_at",
            nullable = false
    )
    private Instant featureCutoffAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "context_json",
            nullable = false)
    private Map<String, Object> contextJson =
            new LinkedHashMap<>();

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    protected AssessmentSubmissionContextEntity() {
    }

    public AssessmentSubmissionContextEntity(
            String id,
            String administrationId,
            Instant featureCutoffAt
    ) {
        this.id = id;
        this.administrationId = administrationId;
        this.featureCutoffAt = featureCutoffAt;
    }

    @PrePersist
    void initializeDefaults() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }

        if (contextJson == null) {
            contextJson = new LinkedHashMap<>();
        }

        if (durationSeconds != null
                && durationSeconds < 0) {
            throw new IllegalStateException(
                    "Duration seconds cannot be negative"
            );
        }
    }

    public void defineAcademicContext(
            String institutionId,
            String campusId,
            String programId,
            String courseId,
            String cohortId,
            String teacherId,
            String grade,
            String academicYear,
            String academicPeriod
    ) {
        this.institutionId = institutionId;
        this.campusId = campusId;
        this.programId = programId;
        this.courseId = courseId;
        this.cohortId = cohortId;
        this.teacherId = teacherId;
        this.grade = grade;
        this.academicYear = academicYear;
        this.academicPeriod = academicPeriod;
    }

    public void defineFieldworkContext(
            String fieldworkPhase,
            String interventionId,
            String interventionGroup,
            String consentId,
            String consentVersion,
            String ethicsProtocol
    ) {
        this.fieldworkPhase = fieldworkPhase;
        this.interventionId = interventionId;
        this.interventionGroup = interventionGroup;
        this.consentId = consentId;
        this.consentVersion = consentVersion;
        this.ethicsProtocol = ethicsProtocol;
    }

    public void defineTechnicalContext(
            String source,
            String deliveryMode,
            String language,
            String deviceType,
            String browser,
            String operatingSystem,
            String timezone,
            String applicationVersion
    ) {
        this.source = source;
        this.deliveryMode = deliveryMode;
        this.language = language;
        this.deviceType = deviceType;
        this.browser = browser;
        this.operatingSystem = operatingSystem;
        this.timezone = timezone;
        this.applicationVersion = applicationVersion;
    }

    public void defineFeatureContext(
            String featureSetVersion,
            String preprocessingVersion,
            String normalizationVersion
    ) {
        this.featureSetVersion = featureSetVersion;
        this.preprocessingVersion = preprocessingVersion;
        this.normalizationVersion = normalizationVersion;
    }

    public void defineTiming(
            Instant startedAt,
            Long durationSeconds
    ) {
        if (durationSeconds != null
                && durationSeconds < 0) {
            throw new IllegalArgumentException(
                    "Duration seconds cannot be negative"
            );
        }

        this.startedAt = startedAt;
        this.durationSeconds = durationSeconds;
    }

    public void replaceContextJson(
            Map<String, Object> contextJson
    ) {
        this.contextJson = contextJson == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(contextJson);
    }

    public String getId() {
        return id;
    }

    public String getAdministrationId() {
        return administrationId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public String getCampusId() {
        return campusId;
    }

    public String getProgramId() {
        return programId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCohortId() {
        return cohortId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getGrade() {
        return grade;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public String getAcademicPeriod() {
        return academicPeriod;
    }

    public String getFieldworkPhase() {
        return fieldworkPhase;
    }

    public String getInterventionId() {
        return interventionId;
    }

    public String getInterventionGroup() {
        return interventionGroup;
    }

    public String getSource() {
        return source;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public String getLanguage() {
        return language;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getBrowser() {
        return browser;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public String getConsentId() {
        return consentId;
    }

    public String getConsentVersion() {
        return consentVersion;
    }

    public String getEthicsProtocol() {
        return ethicsProtocol;
    }

    public String getFeatureSetVersion() {
        return featureSetVersion;
    }

    public String getPreprocessingVersion() {
        return preprocessingVersion;
    }

    public String getNormalizationVersion() {
        return normalizationVersion;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public Instant getFeatureCutoffAt() {
        return featureCutoffAt;
    }

    public Map<String, Object> getContextJson() {
        return Map.copyOf(contextJson);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}