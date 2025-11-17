package com.inclusive.authservice.model;

import jakarta.persistence.*;

/**
 * Modelo para representar diferentes metodos de autenticacion por usuario.
 *
 * Archivo: auth-service/src/main/java/com/inclusive/authservice/model/AuthenticationMethod.java
 */
@Entity
@Table(name = "authentication_methods")
public class AuthenticationMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthMethodType type;

    @Column(name = "primary_method")
    private Boolean primaryMethod = Boolean.FALSE;

    @Column(name = "enabled")
    private Boolean enabled = Boolean.TRUE;

    @Column(name = "phone_number")
    private String phoneNumber;   // para OTP_SMS

    @Column(name = "external_id")
    private String externalId;    // para WebAuthn / OAuth / etc.

    public AuthenticationMethod() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public AuthMethodType getType() { return type; }
    public void setType(AuthMethodType type) { this.type = type; }

    public Boolean getPrimaryMethod() { return primaryMethod; }
    public void setPrimaryMethod(Boolean primaryMethod) { this.primaryMethod = primaryMethod; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
}
