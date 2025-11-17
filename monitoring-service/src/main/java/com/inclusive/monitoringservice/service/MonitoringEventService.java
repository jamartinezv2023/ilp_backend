package com.inclusive.monitoringservice.service;

import com.inclusive.monitoringservice.dto.MonitoringEventDTO;

import java.util.List;

public interface MonitoringEventService {

    List<MonitoringEventDTO> findAll();

    MonitoringEventDTO findById(Long id);

    MonitoringEventDTO create(MonitoringEventDTO dto);

    void delete(Long id);
}