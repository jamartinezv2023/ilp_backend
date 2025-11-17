package com.inclusive.assessmentservice.service;

import com.inclusive.assessmentservice.dto.AssessmentDTO;
import java.util.List;

public interface AssessmentService {

    List<AssessmentDTO> findAll();
    AssessmentDTO findById(Long id);
    AssessmentDTO create(AssessmentDTO dto);
    AssessmentDTO update(Long id, AssessmentDTO dto);
    void delete(Long id);
}