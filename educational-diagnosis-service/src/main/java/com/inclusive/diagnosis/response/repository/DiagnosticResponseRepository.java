package com.inclusive.diagnosis.response.repository;

import com.inclusive.diagnosis.response.entity.DiagnosticResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DiagnosticResponseRepository
        extends JpaRepository<DiagnosticResponse, UUID> {
}
