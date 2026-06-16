package com.inclusive.adaptiveeducationservice.instrument.dto;

import java.util.List;

public record InstrumentQuestionResponse(
        String id,
        String instrument,
        String dimension,
        Integer questionOrder,
        String text,
        List<String> options,
        String instrumentVersion
) {
}