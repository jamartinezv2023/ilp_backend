package com.inclusive.adaptiveeducationservice.assessmentengine.generic.rendering;

public record AssessmentRendererOption(

        String id,

        String code,

        String text,

        String dimension,

        Double numericValue,

        Double weight,

        Integer orderIndex
) {
}