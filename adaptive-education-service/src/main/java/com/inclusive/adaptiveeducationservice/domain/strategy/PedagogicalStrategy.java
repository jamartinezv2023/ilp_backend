package com.inclusive.adaptiveeducationservice.domain.strategy;

import com.inclusive.adaptiveeducationservice.domain.model.StudentCharacterization;

public interface PedagogicalStrategy {
    boolean isApplicable(StudentCharacterization characterization);
    String getRecommendation();
}
