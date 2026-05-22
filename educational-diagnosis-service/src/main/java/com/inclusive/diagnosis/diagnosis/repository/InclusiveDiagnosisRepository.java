package com.inclusive.diagnosis.diagnosis.repository;

import com.inclusive.diagnosis.diagnosis.entity.InclusiveDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InclusiveDiagnosisRepository
        extends JpaRepository<InclusiveDiagnosis, UUID> {
}
