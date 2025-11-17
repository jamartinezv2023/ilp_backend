package com.inclusive.monitoringservice.service.impl;

import com.inclusive.monitoringservice.dto.MonitoringEventDTO;
import com.inclusive.monitoringservice.entity.MonitoringEvent;
import com.inclusive.monitoringservice.mapper.MonitoringEventMapper;
import com.inclusive.monitoringservice.repository.MonitoringEventRepository;
import com.inclusive.monitoringservice.service.MonitoringEventService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MonitoringEventServiceImpl implements MonitoringEventService {

    private final MonitoringEventRepository repository;

    public MonitoringEventServiceImpl(MonitoringEventRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonitoringEventDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(MonitoringEventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MonitoringEventDTO findById(Long id) {
        MonitoringEvent event = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Monitoring event not found"));
        return MonitoringEventMapper.toDto(event);
    }

    @Override
    public MonitoringEventDTO create(MonitoringEventDTO dto) {
        MonitoringEvent entity = MonitoringEventMapper.toNewEntity(dto);
        MonitoringEvent saved = repository.save(entity);
        return MonitoringEventMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Monitoring event not found");
        }
        repository.deleteById(id);
    }
}