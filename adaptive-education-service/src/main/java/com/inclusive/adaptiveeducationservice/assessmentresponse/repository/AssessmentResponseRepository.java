package com.inclusive.adaptiveeducationservice.assessmentresponse.repository;

import com.inclusive.adaptiveeducationservice.assessmentresponse.entity.AssessmentResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentResponseRepository
        extends JpaRepository<AssessmentResponseEntity, String> {

    List<AssessmentResponseEntity> findByStudentIdOrderBySubmittedAtDesc(
            String studentId
    );
}