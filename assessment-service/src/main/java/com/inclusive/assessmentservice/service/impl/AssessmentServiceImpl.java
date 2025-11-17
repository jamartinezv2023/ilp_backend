package com.inclusive.assessmentservice.service.impl;

import com.inclusive.assessmentservice.dto.AssessmentDTO;
import com.inclusive.assessmentservice.entity.Assessment;
import com.inclusive.assessmentservice.repository.AssessmentRepository;
import com.inclusive.assessmentservice.service.AssessmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository repo;

    public AssessmentServiceImpl(AssessmentRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<AssessmentDTO> findAll() {
        return repo.findAll().stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public AssessmentDTO findById(Long id) {
        return repo.findById(id).map(this::map).orElse(null);
    }

    @Override
    public AssessmentDTO create(AssessmentDTO dto) {
        Assessment a = new Assessment();
        a.setUserId(dto.getUserId());
        a.setType(dto.getType());
        return map(repo.save(a));
    }

    @Override
    public AssessmentDTO update(Long id, AssessmentDTO dto) {
        Assessment a = repo.findById(id).orElseThrow();
        a.setUserId(dto.getUserId());
        a.setType(dto.getType());
        return map(repo.save(a));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private AssessmentDTO map(Assessment a) {
        AssessmentDTO dto = new AssessmentDTO();
        dto.setId(a.getId());
        dto.setUserId(a.getUserId());
        dto.setType(a.getType());
        return dto;
    }
}