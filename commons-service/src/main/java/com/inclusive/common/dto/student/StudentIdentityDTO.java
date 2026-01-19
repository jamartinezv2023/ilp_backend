package com.inclusive.common.dto.student;

import java.io.Serializable;

public class StudentIdentityDTO implements Serializable {

    private Long id;
    private String fullName;
    private boolean active;

    public StudentIdentityDTO() {}

    public StudentIdentityDTO(Long id, String fullName, boolean active) {
        this.id = id;
        this.fullName = fullName;
        this.active = active;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public boolean isActive() { return active; }

    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setActive(boolean active) { this.active = active; }
}
