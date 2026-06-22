package com.inclusive.adaptiveeducationservice.fieldwork.dto;

public record FieldworkReadinessResponse(
        boolean ready,
        String message
) {
}
