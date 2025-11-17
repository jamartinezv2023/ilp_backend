package com.inclusive.reportservice.service.impl;

import com.inclusive.reportservice.dto.ReportJobDTO;
import com.inclusive.reportservice.entity.ReportJob;
import com.inclusive.reportservice.mapper.ReportJobMapper;
import com.inclusive.reportservice.repository.ReportJobRepository;
import com.inclusive.reportservice.service.ReportJobService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportJobServiceImpl implements ReportJobService {

    private final ReportJobRepository repository;

    public ReportJobServiceImpl(ReportJobRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportJobDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(ReportJobMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReportJobDTO findById(Long id) {
        ReportJob job = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report job not found"));
        return ReportJobMapper.toDto(job);
    }

    @Override
    public ReportJobDTO create(ReportJobDTO dto) {
        ReportJob job = ReportJobMapper.toNewEntity(dto);
        ReportJob saved = repository.save(job);
        return ReportJobMapper.toDto(saved);
    }

    @Override
    public ReportJobDTO update(Long id, ReportJobDTO dto) {
        ReportJob job = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report job not found"));
        ReportJobMapper.updateEntity(job, dto);
        ReportJob saved = repository.save(job);
        return ReportJobMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report job not found");
        }
        repository.deleteById(id);
    }
}