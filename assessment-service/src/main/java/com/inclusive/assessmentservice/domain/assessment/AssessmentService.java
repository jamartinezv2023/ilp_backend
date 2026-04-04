package com.inclusive.assessmentservice.domain.assessment;

import java.util.List;
import java.util.Optional;

public interface AssessmentService {

    Assessment create(Long studentId, String type);

    Optional<Assessment> findById(Long id);

    List<Assessment> findByStudent(Long studentId);

    Assessment complete(Long assessmentId, Double score, String result);
}
