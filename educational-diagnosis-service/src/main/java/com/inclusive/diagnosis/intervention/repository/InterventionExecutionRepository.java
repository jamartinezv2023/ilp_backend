package com.inclusive.diagnosis.intervention.repository;

import com.inclusive.diagnosis.intervention.entity.InterventionExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterventionExecutionRepository
        extends JpaRepository<InterventionExecution, UUID> {

    List<InterventionExecution> findByStudentProfileId(
            UUID studentProfileId
    );
}
