package com.inclusive.adaptiveeducationservice.domain.adaptation.service;

import com.inclusive.adaptiveeducationservice.domain.model.StudentCharacterization;
import java.util.concurrent.CompletableFuture;

public interface AICharacterizationService {
    // Predicción asíncrona para no bloquear el flujo principal
    CompletableFuture<StudentCharacterization> predictAdvancedNeeds(String studentId, String recentActivityLog);
}
