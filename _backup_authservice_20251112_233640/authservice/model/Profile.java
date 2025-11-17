package com.inclusive.authservice.model;

import javax.persistence.*;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campos detectados (manual):
    // id

    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }
}
