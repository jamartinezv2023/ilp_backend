package com.inclusive.diagnosis.questionnaire.repository;

import com.inclusive.diagnosis.questionnaire.entity.DiagnosticQuestionnaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DiagnosticQuestionnaireRepository
        extends JpaRepository<DiagnosticQuestionnaire, UUID> {
}
