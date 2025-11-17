package com.inclusive.reportservice.service;

import com.inclusive.reportservice.dto.ReportJobDTO;

import java.util.List;

public interface ReportJobService {

    List<ReportJobDTO> findAll();

    ReportJobDTO findById(Long id);

    ReportJobDTO create(ReportJobDTO dto);

    ReportJobDTO update(Long id, ReportJobDTO dto);

    void delete(Long id);
}