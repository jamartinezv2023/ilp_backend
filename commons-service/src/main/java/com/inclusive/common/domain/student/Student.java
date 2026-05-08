package com.inclusive.common.domain.student;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    public static Student create(
            String fullName,
            String email,
            String gender,
            String location
    ) {
        Student student = new Student();
        student.fullName = fullName;
        student.email = email;
        student.gender = gender;
        student.location = location;
        student.active = true;
        return student;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getLocation() {
        return location;
    }

    public boolean isActive() {
        return active;
    }

    public void updateFrom(Student updated) {
        this.fullName = updated.fullName;
        this.email = updated.email;
        this.gender = updated.gender;
        this.location = updated.location;
        this.active = updated.active;
    }

    public void deactivate() {
        this.active = false;
    }
}