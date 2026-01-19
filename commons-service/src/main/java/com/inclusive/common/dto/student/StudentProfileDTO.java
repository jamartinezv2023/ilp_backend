package com.inclusive.common.dto.student;

import java.io.Serializable;

public class StudentProfileDTO implements Serializable {

    private Long id;
    private String fullName;
    private String email;
    private String gender;
    private String location;
    private boolean active;

    public StudentProfileDTO() {}

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
    public String getLocation() { return location; }
    public boolean isActive() { return active; }

    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setGender(String gender) { this.gender = gender; }
    public void setLocation(String location) { this.location = location; }
    public void setActive(boolean active) { this.active = active; }
}
