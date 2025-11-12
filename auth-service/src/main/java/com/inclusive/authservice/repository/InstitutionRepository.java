package com.inclusive.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.authservice.model.Institution;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
}