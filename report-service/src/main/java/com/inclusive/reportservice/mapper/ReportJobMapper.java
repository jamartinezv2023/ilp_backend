package com.inclusive.reportservice.mapper;

import com.inclusive.reportservice.dto.ReportJobDTO;
import com.inclusive.reportservice.entity.ReportJob;

public final class ReportJobMapper {

    private ReportJobMapper() {
    }

    public static ReportJobDTO toDto(ReportJob entity) {
        if (entity == null) {
            return null;
        }
        ReportJobDTO dto = new ReportJobDTO();
        dto.setId(entity.getId());
        dto.setTenantCode(entity.getTenantCode());
        dto.setReportType(entity.getReportType());
        dto.setStatus(entity.getStatus());
        dto.setRequestedBy(entity.getRequestedBy());
        dto.setRequestedAt(entity.getRequestedAt());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setOutputUrl(entity.getOutputUrl());
        dto.setErrorMessage(entity.getErrorMessage());
        return dto;
    }

    public static void updateEntity(ReportJob entity, ReportJobDTO dto) {
        entity.setTenantCode(dto.getTenantCode());
        entity.setReportType(dto.getReportType());
        entity.setStatus(dto.getStatus());
        entity.setRequestedBy(dto.getRequestedBy());
        entity.setRequestedAt(dto.getRequestedAt());
        entity.setCompletedAt(dto.getCompletedAt());
        entity.setOutputUrl(dto.getOutputUrl());
        entity.setErrorMessage(dto.getErrorMessage());
    }

    public static ReportJob toNewEntity(ReportJobDTO dto) {
        ReportJob entity = new ReportJob();
        updateEntity(entity, dto);
        return entity;
    }
}