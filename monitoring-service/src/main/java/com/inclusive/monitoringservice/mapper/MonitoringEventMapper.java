package com.inclusive.monitoringservice.mapper;

import com.inclusive.monitoringservice.dto.MonitoringEventDTO;
import com.inclusive.monitoringservice.entity.MonitoringEvent;

public final class MonitoringEventMapper {

    private MonitoringEventMapper() {
    }

    public static MonitoringEventDTO toDto(MonitoringEvent entity) {
        if (entity == null) {
            return null;
        }
        MonitoringEventDTO dto = new MonitoringEventDTO();
        dto.setId(entity.getId());
        dto.setServiceName(entity.getServiceName());
        dto.setTenantCode(entity.getTenantCode());
        dto.setEventType(entity.getEventType());
        dto.setSeverity(entity.getSeverity());
        dto.setMessage(entity.getMessage());
        dto.setPayload(entity.getPayload());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public static MonitoringEvent toNewEntity(MonitoringEventDTO dto) {
        MonitoringEvent entity = new MonitoringEvent();
        entity.setServiceName(dto.getServiceName());
        entity.setTenantCode(dto.getTenantCode());
        entity.setEventType(dto.getEventType());
        entity.setSeverity(dto.getSeverity());
        entity.setMessage(dto.getMessage());
        entity.setPayload(dto.getPayload());
        return entity;
    }
}