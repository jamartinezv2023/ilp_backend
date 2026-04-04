package com.inclusive.common.domain.student;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String gender;
    private String location;

    private boolean active;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // =========================
    // CONSTRUCTORES
    // =========================

    protected Student() {
        // requerido por JPA
    }

    protected Student(String fullName, String email, String gender, String location) {
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.location = location;
        this.active = true;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    // =========================
    // FACTORY (ÃšNICO punto de creaciÃ³n)
    // =========================

    public static Student create(
            String fullName,
            String email,
            String gender,
            String location
    ) {
        return new Student(fullName, email, gender, location);
    }

    // =========================
    // COMPORTAMIENTO
    // =========================

    public void activate() {
        this.active = true;
        touch();
    }

    public void deactivate() {
        this.active = false;
        touch();
    }

    public void updateFrom(Student other) {
        this.fullName = other.fullName;
        this.email = other.email;
        this.gender = other.gender;
        this.location = other.location;
        touch();
    }

    private void touch() {
        this.updatedAt = OffsetDateTime.now();
    }

    // =========================
    // GETTERS
    // =========================

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
    public String getLocation() { return location; }
    public boolean isActive() { return active; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
