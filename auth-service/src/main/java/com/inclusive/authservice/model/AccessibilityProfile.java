package com.inclusive.authservice.model;

import jakarta.persistence.*;

/**
 * Perfil de accesibilidad vinculado a un usuario.
 *
 * Archivo: auth-service/src/main/java/com/inclusive/authservice/model/AccessibilityProfile.java
 */
@Entity
@Table(name = "accessibility_profiles")
public class AccessibilityProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_disability")
    private DisabilityType primaryDisability = DisabilityType.NONE;

    @Column(name = "needs_sign_language_support")
    private Boolean needsSignLanguageSupport;

    @Column(name = "needs_subtitles")
    private Boolean needsSubtitles;

    @Column(name = "needs_audio_descriptions")
    private Boolean needsAudioDescriptions;

    @Column(name = "motor_impairment")
    private Boolean motorImpairment;

    @Column(name = "cognitive_support_required")
    private Boolean cognitiveSupportRequired;

    @Column(length = 1000)
    private String customNotes;

    public AccessibilityProfile() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public DisabilityType getPrimaryDisability() { return primaryDisability; }
    public void setPrimaryDisability(DisabilityType primaryDisability) { this.primaryDisability = primaryDisability; }

    public Boolean getNeedsSignLanguageSupport() { return needsSignLanguageSupport; }
    public void setNeedsSignLanguageSupport(Boolean needsSignLanguageSupport) { this.needsSignLanguageSupport = needsSignLanguageSupport; }

    public Boolean getNeedsSubtitles() { return needsSubtitles; }
    public void setNeedsSubtitles(Boolean needsSubtitles) { this.needsSubtitles = needsSubtitles; }

    public Boolean getNeedsAudioDescriptions() { return needsAudioDescriptions; }
    public void setNeedsAudioDescriptions(Boolean needsAudioDescriptions) { this.needsAudioDescriptions = needsAudioDescriptions; }

    public Boolean getMotorImpairment() { return motorImpairment; }
    public void setMotorImpairment(Boolean motorImpairment) { this.motorImpairment = motorImpairment; }

    public Boolean getCognitiveSupportRequired() { return cognitiveSupportRequired; }
    public void setCognitiveSupportRequired(Boolean cognitiveSupportRequired) { this.cognitiveSupportRequired = cognitiveSupportRequired; }

    public String getCustomNotes() { return customNotes; }
    public void setCustomNotes(String customNotes) { this.customNotes = customNotes; }
}
