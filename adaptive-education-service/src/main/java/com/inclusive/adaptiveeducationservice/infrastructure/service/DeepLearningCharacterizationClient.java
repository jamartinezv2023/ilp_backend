package com.inclusive.adaptiveeducationservice.infrastructure.service;
import com.inclusive.adaptiveeducationservice.domain.model.StudentCharacterization;
import org.springframework.stereotype.Service;

@Service
public class DeepLearningCharacterizationClient {
    public StudentCharacterization predictAdvancedNeeds(String id, String log) {
        return new StudentCharacterization();
    }
}
